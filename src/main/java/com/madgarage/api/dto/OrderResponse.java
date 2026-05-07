package com.madgarage.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Double discountAmount;
    private String appliedCouponCode;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;
    
    @com.fasterxml.jackson.annotation.JsonProperty("isOwner")
    private boolean owner;

    public boolean isOwner() { return owner; }
    public void setOwner(boolean owner) { this.owner = owner; }
    
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
 
    private boolean active;
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
        
        @com.fasterxml.jackson.annotation.JsonProperty("isReturnable")
        private boolean returnable;

        public boolean isReturnable() { return returnable; }
        public void setReturnable(boolean returnable) { this.returnable = returnable; }
    }
}