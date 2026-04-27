package com.madgarage.api.services;

import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRatingRepository;
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
                        .condition(item.getProduct() != null && item.getProduct().getCondition() != null ? item.getProduct().getCondition().name() : "NEW")
                        .color(item.getProduct() != null ? item.getProduct().getColor() : "N/A")
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
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

        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .customerName(custName)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .shippingFee(shipFee)
                .platformFee(order.getPlatformFee() != null ? order.getPlatformFee() : 0.0)
                .grandTotal(grandTotal)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
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
                builder.fittingGarageName(garage.getFullName());
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
}
