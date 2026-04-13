package com.madgarage.api.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

@Data
public class OrderRequest {
    private List<CartItemDto> items;
    
    // Shipping Details
    private String shippingAddress;
    private String city;
    private String state;
    private String pincode;

    @Data
    public static class CartItemDto {
        private Long productId;
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Cannot order more than 100 units")
        private Integer quantity;
    }
}