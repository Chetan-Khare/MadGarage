package com.madgarage.api.dto;
import lombok.Data;

@Data
public class GarageProductDTO {
    private Long id;
    private String name;
    private Double originalPrice;
    private Double garagePrice; // The 5% off price
    private String imageUrl;
}
