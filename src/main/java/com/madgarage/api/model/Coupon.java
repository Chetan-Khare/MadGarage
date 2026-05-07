package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType discountType;

    @Column(nullable = false)
    private Double discountAmount;

    private Double minOrderAmount;

    private Double maxDiscountAmount;

    private Integer usageLimit;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private Integer maxUsagePerUser = 1;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum CouponType {
        PERCENTAGE, FIXED
    }
}
