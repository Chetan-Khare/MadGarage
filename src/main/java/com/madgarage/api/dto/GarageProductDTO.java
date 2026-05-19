package com.madgarage.api.dto;
import lombok.Data;
import java.util.List;
import com.madgarage.api.enums.ShippingClass;

@Data
public class GarageProductDTO {
    private Long id;
    private String partName;
    private Double originalPrice;
    private Double garagePrice; // The 5% off price
    private String imageUrl;
    private String condition;
    private String color;
    private String category;
    private String brand;
    private Integer stockQuantity;
    private List<String> imageUrls;
    private Double rating;
    private boolean wholesale;
    private Double mrp;
    private Double discountPercentage;
    private ShippingClass shippingClass;
    private Double weightKg;
    private Double customShippingCost;
    private String sellerState;
}
