package com.madgarage.api.services;

import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRatingRepository;
import com.madgarage.api.repository.ReturnRepository;
import com.madgarage.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    
    private final UserRepository userRepository;
    private final OrderRatingRepository orderRatingRepository;
    private final ReturnRepository returnRepository;

    public List<OrderResponse> mapToOrderResponses(List<Order> orders, User requester) {
        if (orders == null || orders.isEmpty()) return List.of();

        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<Long> garageIds = orders.stream()
                .map(Order::getFittingGarageId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Batch prefetch returns
        java.util.Map<Long, List<com.madgarage.api.model.ReturnRequest>> returnsByOrderId = returnRepository.findAllByOrderIdIn(orderIds).stream()
                .collect(Collectors.groupingBy(r -> r.getOrder().getId()));

        // Batch prefetch garages
        java.util.Map<Long, User> garagesById = garageIds.isEmpty() ? java.util.Map.of() :
                userRepository.findAllById(garageIds).stream()
                .collect(Collectors.toMap(User::getId, g -> g));

        // Batch prefetch ratings
        java.util.Map<Long, com.madgarage.api.model.OrderRating> ratingsByOrderId = orderRatingRepository.findAllByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(r -> r.getOrder().getId(), r -> r, (r1, r2) -> r1));

        return orders.stream()
                .map(order -> mapToOrderResponseInternal(order, requester, 
                        returnsByOrderId.getOrDefault(order.getId(), List.of()),
                        order.getFittingGarageId() != null ? garagesById.get(order.getFittingGarageId()) : null,
                        ratingsByOrderId.get(order.getId())))
                .collect(Collectors.toList());
    }

    public OrderResponse mapToOrderResponse(Order order, User requester) {
        List<com.madgarage.api.model.ReturnRequest> returns = returnRepository.findAllByOrderId(order.getId());
        User garage = order.getFittingGarageId() != null ? userRepository.findById(order.getFittingGarageId()).orElse(null) : null;
        com.madgarage.api.model.OrderRating rating = orderRatingRepository.findByOrderId(order.getId()).orElse(null);
        return mapToOrderResponseInternal(order, requester, returns, garage, rating);
    }

    private OrderResponse mapToOrderResponseInternal(Order order, User requester, 
                                                    List<com.madgarage.api.model.ReturnRequest> returns, 
                                                    User garage, 
                                                    com.madgarage.api.model.OrderRating rating) {
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
                        .condition(item.getProduct() != null && item.getProduct().getCondition() != null ? item.getProduct().getCondition().name() : "NEW")
                        .color(item.getProduct() != null ? item.getProduct().getColor() : "N/A")
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .returnable(item.getProduct() != null ? item.getProduct().isReturnable() : true)
                        .build())
                .collect(Collectors.toList());

        String custName = "";
        if (order.getUser() != null) {
            custName = order.getUser().getFullName();
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

        Long ownerId = (order.getUser() != null) ? order.getUser().getId() : null;
        Long reqId = (requester != null) ? requester.getId() : null;
        // LOW-06 FIX: Restrict isOwner check strictly to matching user IDs (do not conflate admin role as owner)
        boolean isOwner = (ownerId != null && ownerId.equals(reqId));

        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .customerName(custName)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(shipFee)
                .platformFee(order.getPlatformFee() != null ? order.getPlatformFee() : 0.0)
                .grandTotal(grandTotal)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .appliedCouponCode(order.getAppliedCouponCode())
                .discountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0)
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress())
                .city(order.getCity())
                .state(order.getState())
                .pincode(order.getPincode())
                .deliveryType(order.getDeliveryType())
                .fittingGarageId(order.getFittingGarageId())
                .fittingStatus(order.getFittingStatus())
                .owner(isOwner)
                .active(order.isActive())
                .items(itemResponses);

        // Map Return Details
        com.madgarage.api.model.ReturnRequest activeReturn = returns.stream()
                .filter(r -> r.getStatus() != com.madgarage.api.enums.ReturnStatus.REJECTED)
                .findFirst()
                .orElse(returns.stream()
                        .filter(r -> r.getStatus() == com.madgarage.api.enums.ReturnStatus.REJECTED)
                        .reduce((first, second) -> second)
                        .orElse(null));

        if (activeReturn != null) {
            builder.activeReturnId(activeReturn.getId());
            builder.returnReason(activeReturn.getReason() != null ? activeReturn.getReason().name() : "N/A");
            builder.returnDescription(activeReturn.getDescription());
            builder.returnStatus(activeReturn.getStatus() != null ? activeReturn.getStatus().name() : "PENDING");
            builder.returnRequestType(activeReturn.getRequestType() != null ? activeReturn.getRequestType().name() : "REFUND");
            builder.returnAdminNote(activeReturn.getAdminNote());
            builder.adminNote(activeReturn.getAdminNote());
        }

        // Fetch garage details if it's a fitting order
        if (order.getFittingGarageId() != null && garage != null) {
            builder.fittingGarageName(garage.getFullName());
            builder.fittingGarageAddress((garage.getAddress() != null ? garage.getAddress() + ", " : "") + garage.getCity());
        }

        // Fetch rating if exists
        if (rating != null) {
            builder.partRating(rating.getPartRating());
            builder.deliveryRating(rating.getDeliveryRating());
            builder.ratingComment(rating.getComment());
        }

        return builder.build();
    }
}
