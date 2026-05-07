package com.madgarage.api.dto;

import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.enums.PartCondition;
import lombok.Data;

@Data
public class ProductRequest {
    private String sku;
    private String brand;
    private String partName;
    private String category;
    private Double price;
    private Double mrp;
    private Double discountPercentage;
    private String description;
    private String imageUrl;
    private String color;
    private Integer stockQuantity;
    private FitmentCategory fitmentCategory;
    private PartCondition condition;
    private String installationGuideUrl;
    private Long sellerId;
    private boolean isManualRating;
    private Double rating;
    private Boolean flagged;
    private String flagReason;
    private String sellerResponse;
    private Boolean wholesale;
}
