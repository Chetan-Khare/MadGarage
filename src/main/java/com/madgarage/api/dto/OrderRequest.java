package com.madgarage.api.dto;

import lombok.Data;
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
        private Integer quantity;
    }
}