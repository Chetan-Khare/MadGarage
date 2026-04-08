package com.madgarage.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WishlistItemResponse {
    private Long productId;
    private String name;
    private String brand;
    private Double price;
    private String imageUrl;
    private String category;
    private String condition;
    private LocalDateTime addedAt;
}
