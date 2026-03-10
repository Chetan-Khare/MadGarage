package com.madgarage.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Double totalAmount;
    private List<CartItemDto> items;

    @Data
    public static class CartItemDto {
        private Long productId;
        private Integer quantity;
        private Double price;
    }
}