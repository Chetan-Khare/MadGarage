package com.madgarage.api.dto;

import com.madgarage.api.model.Coupon.CouponType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotBlank(message = "Coupon code is mandatory")
    private String code;

    private String description;

    @NotNull(message = "Discount type is mandatory")
    private CouponType discountType;

    @NotNull(message = "Discount amount is mandatory")
    @Min(value = 0, message = "Discount amount cannot be negative")
    private Double discountAmount;

    @Min(value = 0, message = "Minimum order amount cannot be negative")
    private Double minOrderAmount;

    @Min(value = 0, message = "Maximum discount amount cannot be negative")
    private Double maxDiscountAmount;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Min(value = 1, message = "Max usage per user must be at least 1")
    private Integer maxUsagePerUser;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isActive;
}
