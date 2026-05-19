package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.enums.PartCondition;
import com.madgarage.api.enums.ShippingClass;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// ARCH-05 FIX: Added indexes on frequently queried columns to prevent full table scans
@Table(name = "products", indexes = {
    @Index(name = "idx_product_seller", columnList = "seller_id"),
    @Index(name = "idx_product_fitment", columnList = "fitment_category")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    private String brand;
    private String partName;

    private String category; // e.g., "Brakes", "Filters"
    private Double price; // e.g., 1250.00
    private Double mrp; // Maximum Retail Price (Original Price)
    private Double discountPercentage; // Optional individual discount for garages

    @Column(length = 1000) // Allows for longer descriptions
    private String description;

    private String imageUrl;
    private String color;
    private Integer stockQuantity;
    private String installationGuideUrl; // Path to the PDF file (e.g., /guides/brake_install.pdf)
    
    @Builder.Default
    private boolean manualRatingOverride = false;
    private Double manualRating;
    
    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean wholesale = true;

    @Builder.Default
    private boolean flagged = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isReturnable = true;

    @Column(length = 1000)
    private String flagReason;

    @Column(length = 1000)
    private String sellerResponse;

    @Enumerated(EnumType.STRING)
    private FitmentCategory fitmentCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_condition")
    private PartCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_class", nullable = false)
    @Builder.Default
    private ShippingClass shippingClass = ShippingClass.STANDARD;

    @Column(name = "weight_kg")
    @Builder.Default
    private Double weightKg = 1.0;

    @Column(name = "custom_shipping_cost")
    private Double customShippingCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    private User seller;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "product_fitment", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "vehicle_id"))

    @Builder.Default // Prevents Lombok Builder from overriding this with null
    private Set<Vehicle> fittedVehicles = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<ProductImage> images = new ArrayList<>();
}