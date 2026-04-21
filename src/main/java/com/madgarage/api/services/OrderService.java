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
    private final UserRepository userRepository;

    // SCALE-03 FIX: Shipping fee from config, not hardcoded
    @Value("${app.pricing.shipping-fee:250.0}")
    private double shippingFee;

    @Value("${app.pricing.platform-fee:7.0}")
    private double platformFee;

    public List<OrderResponse> getCustomerOrders(User customer) {
        // ARCH-03 FIX: Uses JOIN FETCH to load everything in one SQL query
        List<Order> rawOrders = orderRepository.findByUserWithItems(customer);
        return rawOrders.stream()
                .map(order -> mapToOrderResponse(order, customer))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getSellerOrders(User seller) {
        return orderRepository.findAllBySeller(seller).stream()
                .map(order -> mapToOrderResponse(order, seller))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrdersAsDto() {
        return orderRepository.findAll().stream()
                .map(order -> mapToOrderResponse(order, null))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getGarageFittings(User garage) {
        return orderRepository.findByFittingGarageIdWithItems(garage.getId()).stream()
                .map(order -> mapToOrderResponse(order, garage))
                .collect(Collectors.toList());
    }

    public OrderResponse mapToOrderResponse(Order order, User requester) {
        boolean isSeller = requester != null && requester.getRole() == com.madgarage.api.enums.Role.ROLE_SELLER;

        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .filter(item -> {
                    if (isSeller) {
                        return item.getProduct() != null && 
                               item.getProduct().getSeller() != null && 
                               item.getProduct().getSeller().getId().equals(requester.getId());
                    }
                    return true;
                })
                .map(item -> (OrderResponse.OrderItemResponse) OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getPartName() : "Unknown Part")
                        .productImageUrl(item.getProduct() != null ? item.getProduct().getImageUrl() : "")
                        .condition(item.getProduct() != null ? item.getProduct().getCondition().name() : "NEW")
                        .color(item.getProduct() != null ? item.getProduct().getColor() : "N/A")
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build())
                .collect(Collectors.toList());

        String custName = "";
        if (order.getUser() != null) {
            custName = (order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "") + " "
                    + (order.getUser().getLastName() != null ? order.getUser().getLastName() : "");
            custName = custName.trim();
        }

        double subtotal = order.getSubtotal();
        double taxAmount = order.getTaxAmount();
        double shipFee = order.getShippingFee();
        double grandTotal = order.getGrandTotal();

        // If a seller is viewing, they only see the total for their items
        if (isSeller) {
            subtotal = itemResponses.stream()
                    .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                    .sum();
            taxAmount = 0.0; // Tax is already included in product price
            shipFee = 0.0; // Shipping info is for the customer/admin view
            grandTotal = subtotal; // Sellers don't receive the platform fee
        }

        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .customerName(custName)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(shipFee)
                .platformFee(order.getPlatformFee() != null ? order.getPlatformFee() : 0.0)
                .grandTotal(grandTotal)
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress())
                .city(order.getCity())
                .state(order.getState())
                .pincode(order.getPincode())
                .deliveryType(order.getDeliveryType())
                .fittingGarageId(order.getFittingGarageId())
                .fittingStatus(order.getFittingStatus())
                .isOwner(requester != null && order.getUser() != null && order.getUser().getId().equals(requester.getId()))
                .items(itemResponses);

        // Fetch garage details if it's a fitting order
        if (order.getFittingGarageId() != null) {
            userRepository.findById(order.getFittingGarageId()).ifPresent(garage -> {
                builder.fittingGarageName(garage.getFirstName() + " " + garage.getLastName());
                builder.fittingGarageAddress((garage.getAddress() != null ? garage.getAddress() + ", " : "") + garage.getCity());
            });
        }

        // Fetch rating if exists
        orderRatingRepository.findByOrderId(order.getId()).ifPresent(rating -> {
            builder.partRating(rating.getPartRating());
            builder.deliveryRating(rating.getDeliveryRating());
            builder.ratingComment(rating.getComment());
        });

        return builder.build();
    }

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderRatingRepository orderRatingRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderRatingRepository = orderRatingRepository;
        this.userRepository = userRepository;
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
            subtotal += itemPrice * itemDto.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(itemPrice)
                    .build();
            orderItems.add(orderItem);
        }

        double taxAmount = 0.0; // Tax is already included in product price
        double grandTotal = subtotal + shippingFee + platformFee;

        Order order = Order.builder()
                .user(customer)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(shippingFee)
                .platformFee(platformFee)
                .grandTotal(grandTotal)
                .status("PAID")
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
        return mapToOrderResponse(order, customer);
    }

    // SEC-06 FIX: Uses JOIN FETCH so order.getUser() is never null/lazy-proxy
    // This was the root cause of the receipt 403 Forbidden error.
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithUser(id).orElse(null);
    }

    private static final java.util.List<String> ALLOWED_STATUSES = java.util.Arrays.asList("PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED");

    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));
        
        String canonicalStatus = newStatus.toUpperCase().trim();
        if (!ALLOWED_STATUSES.contains(canonicalStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + newStatus);
        }

        order.setStatus(canonicalStatus);
        orderRepository.save(order);
        return mapToOrderResponse(order, order.getUser());
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
}
