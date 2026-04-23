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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderRatingRepository orderRatingRepository;
    private final PricingService pricingService;
    private final SystemSettingService systemSettingService;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, 
                        OrderRatingRepository orderRatingRepository, PricingService pricingService, 
                        SystemSettingService systemSettingService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderRatingRepository = orderRatingRepository;
        this.pricingService = pricingService;
        this.systemSettingService = systemSettingService;
        this.orderMapper = orderMapper;
    }


    public List<OrderResponse> getCustomerOrders(User customer) {
        // ARCH-03 FIX: Uses JOIN FETCH to load everything in one SQL query
        List<Order> rawOrders = orderRepository.findByUserWithItems(customer);
        return rawOrders.stream()
                .map(order -> orderMapper.mapToOrderResponse(order, customer))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getSellerOrders(User seller) {
        return orderRepository.findAllBySeller(seller).stream()
                .map(order -> orderMapper.mapToOrderResponse(order, seller))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrdersAsDto() {
        return orderRepository.findAll().stream()
                .map(order -> orderMapper.mapToOrderResponse(order, null))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getGarageFittings(User garage) {
        return orderRepository.findByFittingGarageIdWithItems(garage.getId()).stream()
                .map(order -> orderMapper.mapToOrderResponse(order, garage))
                .collect(Collectors.toList());
    }


    @Transactional // If anything fails, it rolls back the whole database transaction
    public OrderResponse placeOrder(User customer, OrderRequest request) {
        double subtotal = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.CartItemDto itemDto : request.getItems()) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity.");
            }

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + itemDto.getProductId()));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for: " + product.getPartName());
            }

            // Deduct stock
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
        double currentPlatformFee = systemSettingService.getSettingDouble("PLATFORM_FEE", 7.0);

        double grandTotal = subtotal + currentShippingFee + currentPlatformFee;

        Order order = Order.builder()
                .user(customer)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(currentShippingFee)
                .platformFee(currentPlatformFee)
                .grandTotal(grandTotal)
                .status("PENDING_PAYMENT")
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

    @Value("${app.jwt.secret}")
    private String paymentSecret; // Reusing JWT secret for mock signature validation

    @Transactional
    public OrderResponse verifyPayment(Long orderId, String paymentId, String signature) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in pending payment state.");
        }

        // HMAC-SHA256 Verification Protocol
        // In production, this would use the payment gateway's public key or secret.
        String expectedSignature = calculateMockSignature(orderId, paymentId);
        
        if (!expectedSignature.equals(signature)) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Security Verification Failed: Tampered payment signature detected.");
        }

        order.setStatus("PAID");
        order.setPaymentId(paymentId);
        order.setPaymentSignature(signature);
        order.setPaymentVerified(true);
        
        order = orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, order.getUser());
    }

    private String calculateMockSignature(Long orderId, String paymentId) {
        // Reduced complexity for the security demonstration: Base64(orderId|paymentId)
        // In a production environment, this MUST be a server-side HMAC-SHA256 using 
        // the payment provider's secret key.
        String data = orderId + "|" + paymentId;
        return java.util.Base64.getEncoder().encodeToString(data.getBytes());
    }

    // SEC-06 FIX: Uses JOIN FETCH so order.getUser() is never null/lazy-proxy
    // This was the root cause of the receipt 403 Forbidden error.
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithUser(id).orElse(null);
    }

    private static final java.util.List<String> ALLOWED_STATUSES = java.util.Arrays.asList("PENDING", "PAID", "SHIPPED", "ARRIVED_AT_GARAGE", "DELIVERED", "CANCELLED");

    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        
        String canonicalStatus = newStatus.toUpperCase().trim();
        if (!ALLOWED_STATUSES.contains(canonicalStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + newStatus);
        }

        order.setStatus(canonicalStatus);
        orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, order.getUser());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        
        // Restore stock when an order is completely deleted (optional business rule, but logical for an inventory system)
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                productRepository.save(p);
            }
        }
        
        orderRepository.delete(order);
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
    public OrderResponse updateFittingStatus(Long orderId, String newFittingStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        String canonical = newFittingStatus.toUpperCase().trim();
        
        // If the garage marks it as arrived, we update the main status too
        if ("ARRIVED_AT_GARAGE".equals(canonical)) {
            order.setStatus("ARRIVED_AT_GARAGE");
            order.setFittingStatus("PENDING_INSPECTION");
        } else if ("COMPLETED".equals(canonical)) {
            order.setStatus("DELIVERED");
            order.setFittingStatus("COMPLETED");
        } else {
            order.setFittingStatus(canonical);
        }

        orderRepository.save(order);
        return orderMapper.mapToOrderResponse(order, null);
    }
}
