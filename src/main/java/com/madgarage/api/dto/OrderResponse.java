package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String customerName; // Added to avoid full User entity leak
    private Double subtotal;
    private Double taxAmount;
    private Double shippingFee;
    private Double platformFee;
    private Double grandTotal;
    private String status;
    private LocalDateTime orderDate;
    private boolean isOwner; // Distinguishes between customer and merchant view
    
    // Shipping Details
    private String shippingAddress;
    private String city;
    private String state;
    private String pincode;

    private String deliveryType;
    private Long fittingGarageId;
    private String fittingStatus;
    private String fittingGarageName;
    private String fittingGarageAddress;

    // Rating Details (Optional)
    private Integer partRating;
    private Integer deliveryRating;
    private String ratingComment;

    private List<OrderItemResponse> items; // Added to map OrderItems securely

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private String condition;
        private String color;
        private Integer quantity;
        private Double priceAtPurchase;
    }
}