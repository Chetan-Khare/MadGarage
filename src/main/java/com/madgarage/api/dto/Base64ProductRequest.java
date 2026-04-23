package com.madgarage.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class Base64ProductRequest {
    private String sku;
    private String brand;
    private String partName;
    private String category;
    private Double price;
    private String description;
    private Integer stockQuantity;
    private String color;
    private String condition;
    private String fitmentCategory;
    private List<Long> vehicleIds;
    
    // List of images as base64 strings
    private List<String> base64Images;
    
    // Optional base64 installation guide
    private String base64Guide;
    private String guideExtension;
    private Boolean flagged;
    private String sellerResponse;
    private Boolean isManualRating;
    private Double rating;
    private String flagReason;
    private Boolean wholesale;
}
