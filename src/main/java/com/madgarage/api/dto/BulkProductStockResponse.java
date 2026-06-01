package com.madgarage.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO returned by GET /api/products/bulk for cart stock validation.
 * Both 'active' and 'isActive' fields are emitted so that mobile and web
 * clients can check either key without breaking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkProductStockResponse {
    private Long id;
    private Integer stockQuantity;
    /** Serialized as "active" in JSON. */
    private boolean active;
    /** Serialized as "isActive" in JSON (Lombok getter: isIsActive → "isActive"). */
    private boolean isActive;
}
