package com.madgarage.api.services;

import com.madgarage.api.dto.OrderRequest;
import com.madgarage.api.dto.OrderResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.madgarage.api.model.*;
import com.madgarage.api.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.madgarage.api.enums.Role;
import com.madgarage.api.enums.OrderStatus;
import com.madgarage.api.enums.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderRatingRepository orderRatingRepository;
    private final PricingService pricingService;
    private final SystemSettingService systemSettingService;
    private final OrderMapper orderMapper;
    private final RazorpayService razorpayService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, 
                        OrderRatingRepository orderRatingRepository, PricingService pricingService, 
                        SystemSettingService systemSettingService, OrderMapper orderMapper,
                        RazorpayService razorpayService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderRatingRepository = orderRatingRepository;
        this.pricingService = pricingService;
        this.systemSettingService = systemSettingService;
        this.orderMapper = orderMapper;
        this.razorpayService = razorpayService;
    }


    public List<OrderResponse> getCustomerOrders(User customer) {
        // ARCH-03 FIX: Uses JOIN FETCH to load everything in one SQL query
        List<Order> rawOrders = orderRepository.findByUserWithItems(customer);
        return rawOrders.stream()
                .filter(order -> order.isActive() && order.getStatus() != OrderStatus.PENDING_PAYMENT)
                .map(order -> orderMapper.mapToOrderResponse(order, customer))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getSellerOrders(User seller) {
        return orderRepository.findAllBySeller(seller).stream()
                .filter(Order::isActive)
                .map(order -> orderMapper.mapToOrderResponse(order, seller))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrdersAsDto() {
        return orderRepository.findAllWithItems().stream()
                .filter(order -> order.isActive() && order.getStatus() != OrderStatus.PENDING_PAYMENT)
                .map(order -> orderMapper.mapToOrderResponse(order, null))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getGarageFittings(User garage) {
        return orderRepository.findByFittingGarageIdWithItems(garage.getId()).stream()
                .filter(Order::isActive)
                .map(order -> orderMapper.mapToOrderResponse(order, garage))
                .collect(Collectors.toList());
    }


    @Transactional
    public OrderResponse placeOrder(User customer, OrderRequest request) {
        double subtotal = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.CartItemDto itemDto : request.getItems()) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity.");
            }

            // SEC-09 FIX: Use Pessimistic Lock to prevent overselling during high-concurrency spikes
            Product product = productRepository.findByIdWithLock(itemDto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + itemDto.getProductId()));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for: " + product.getPartName());
            }

            // Deduct stock safely within the locked transaction
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            double itemPrice = product.getPrice();

            // Apply tiered garage discount if the customer is a garage
            boolean isGarage = customer.getRole() == com.madgarage.api.enums.Role.ROLE_GARAGE;
            if (isGarage) {
                itemPrice = pricingService.calculateGaragePrice(product);
            }

            subtotal += itemPrice * itemDto.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(itemPrice)
                    .build();
            orderItems.add(orderItem);
        }

        double taxAmount = 0.0; // Tax is already included in product price
        
        double currentShippingFee = systemSettingService.getSettingDouble("SHIPPING_FEE", 250.0);
        double freeShippingThreshold = systemSettingService.getSettingDouble("FREE_SHIPPING_THRESHOLD", 400.0);
        double currentPlatformFee = systemSettingService.getSettingDouble("PLATFORM_FEE", 7.0);

        if (subtotal >= freeShippingThreshold) {
            currentShippingFee = 0.0;
        }

        double grandTotal = subtotal + currentShippingFee + currentPlatformFee;

        Order order = Order.builder()
                .user(customer)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(currentShippingFee)
                .platformFee(currentPlatformFee)
                .grandTotal(grandTotal)
                .status(OrderStatus.PENDING_PAYMENT)
                .orderDate(LocalDateTime.now())
                .shippingAddress(request.getShippingAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .deliveryType(request.getDeliveryType() != null ? request.getDeliveryType() : "HOME_DELIVERY")
                .fittingGarageId(request.getFittingGarageId())
                .fittingStatus("GARAGE_FITTING".equals(request.getDeliveryType()) ? "PENDING_INSPECTION" : "NONE")
                .build();

        for (OrderItem oi : orderItems) {
            order.addOrderItem(oi);
        }

        order = orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, customer);
    }

    @Transactional
    public void setRazorpayOrderId(Long orderId, String rzpOrderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        order.setRazorpayOrderId(rzpOrderId);
        orderRepository.save(order);
    }

    @Value("${app.jwt.secret}")
    private String paymentSecret; // Reusing JWT secret for mock signature validation

    @Transactional
    public OrderResponse verifyPayment(Long orderId, String paymentId, String signature) {
        // SEC-09 FIX: Use Pessimistic Lock to prevent race conditions during verification
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already processed or cancelled.");
        }

        // Signature Verification
        String rzpOrderId = order.getRazorpayOrderId();
        
        if (rzpOrderId == null || rzpOrderId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment cannot be verified: Razorpay order ID not set for this order.");
        }
        
        boolean isValid = razorpayService.verifySignature(rzpOrderId, paymentId, signature);

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Security Verification Failed: Tampered payment signature detected.");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaymentId(paymentId);
        order.setPaymentSignature(signature);
        order.setPaymentVerified(true);
        
        order = orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, order.getUser());
    }

    // SEC-06 FIX: Uses JOIN FETCH so order.getUser() is never null/lazy-proxy
    // This was the root cause of the receipt 403 Forbidden error.
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus, User requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        
        OrderStatus currentStatus = order.getStatus();
        OrderStatus targetStatus;
        try {
            targetStatus = OrderStatus.valueOf(newStatus.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + newStatus);
        }

        if (currentStatus == targetStatus) return orderMapper.mapToOrderResponse(order, order.getUser());

        // 1. Verify the transition is valid (Staff can override anything)
        boolean isStaff = requester.getRole() == Role.ROLE_ADMIN || requester.getRole() == Role.ROLE_WORKER;
        if (!isStaff && !isValidTransition(currentStatus, targetStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid state transition from " + currentStatus + " to " + targetStatus);
        }

        // 2. Verify the user has the AUTHORITY to make THIS specific transition
        if (!hasAuthorityForTransition(requester.getRole(), currentStatus, targetStatus)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User role " + requester.getRole() + " is not authorized to transition order to " + targetStatus);
        }

        // Action: Restore stock if cancelling
        if (targetStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(targetStatus);
        order = orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, order.getUser());
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus target) {
        return switch (current) {
            case PENDING_PAYMENT -> target == OrderStatus.PAID || target == OrderStatus.CANCELLED;
            case PAID -> target == OrderStatus.PROCESSING || target == OrderStatus.CANCELLED || target == OrderStatus.SHIPPED;
            case PROCESSING -> target == OrderStatus.SHIPPED || target == OrderStatus.CANCELLED;
            case SHIPPED -> target == OrderStatus.ARRIVED_AT_GARAGE || target == OrderStatus.DELIVERED || target == OrderStatus.CANCELLED;
            case ARRIVED_AT_GARAGE -> target == OrderStatus.DELIVERED || target == OrderStatus.CANCELLED;
            case DELIVERED -> false; // Final state
            case CANCELLED -> false; // Final state
        };
    }

    private boolean hasAuthorityForTransition(Role role, OrderStatus current, OrderStatus target) {
        // Staff (Admin/Worker) have full authority over status transitions
        if (role == Role.ROLE_ADMIN || role == Role.ROLE_WORKER) return true;

        return switch (target) {
            case PAID -> false; // Only via system verifyPayment hook
            case PROCESSING -> role == Role.ROLE_SELLER;
            case SHIPPED -> role == Role.ROLE_SELLER;
            case ARRIVED_AT_GARAGE -> role == Role.ROLE_GARAGE;
            case DELIVERED -> role == Role.ROLE_SELLER || role == Role.ROLE_GARAGE;
            case CANCELLED -> (role == Role.ROLE_CUSTOMER && current == OrderStatus.PENDING_PAYMENT);
            default -> false;
        };
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            }
        }
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        
        restoreStock(order);
        order.setActive(false);
        orderRepository.save(order);
    }

    @Transactional
    public void saveRating(Long orderId, com.madgarage.api.dto.OrderRatingRequest request, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        if (!order.getUser().getId().equals(customer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only rate your own orders.");
        }

        if (orderRatingRepository.existsByOrderId(orderId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback already submitted for this order.");
        }

        OrderRating rating = OrderRating.builder()
                .order(order)
                .partRating(request.getPartRating())
                .deliveryRating(request.getDeliveryRating())
                .comment(request.getComment())
                .build();

        orderRatingRepository.save(rating);
    }

    @Transactional
    public OrderResponse updateFittingStatus(Long orderId, String newFittingStatus, User requester) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        String canonical = newFittingStatus.toUpperCase().trim();
        
        // If the garage marks it as arrived, we update the main status too
        if ("ARRIVED_AT_GARAGE".equals(canonical)) {
            updateOrderStatus(orderId, "ARRIVED_AT_GARAGE", requester);
            order.setFittingStatus("PENDING_INSPECTION");
        } else if ("COMPLETED".equals(canonical)) {
            updateOrderStatus(orderId, "DELIVERED", requester);
            order.setFittingStatus("COMPLETED");
        } else {
            order.setFittingStatus(canonical);
        }

        orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, null);
    }

    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    @Transactional
    public void cleanupStalePendingOrders() {
        // Restore stock for orders abandoned in PENDING_PAYMENT for > 30 minutes
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<Order> staleOrders = orderRepository.findByStatusAndOrderDateBefore(OrderStatus.PENDING_PAYMENT, cutoff);
        
        for (Order order : staleOrders) {
            restoreStock(order);
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}
