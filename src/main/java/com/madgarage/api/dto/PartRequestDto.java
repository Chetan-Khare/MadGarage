package com.madgarage.api.dto;

import lombok.Data;

@Data
public class PartRequestDto {
    private String make;
    private String model;
    private Integer year;
    private String partName;
    private String description;
    
    // Optional fields for Guest users
    private String customerName;
    private String customerPhone;
}
