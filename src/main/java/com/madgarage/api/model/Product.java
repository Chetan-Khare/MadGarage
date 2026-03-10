package com.madgarage.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    private String brand;
    private String partName;

    private String category; // e.g., "Brakes", "Filters"
    private Double price;    // e.g., 1250.00

    @Column(length = 1000) // Allows for longer descriptions
    private String description;

    private String imageUrl;
    private String color;
    private Integer stockQuantity;
    private String installationGuideUrl; // Path to the PDF file (e.g., /guides/brake_install.pdf)

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "product_fitment",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )

    @Builder.Default // Prevents Lombok Builder from overriding this with null
    private Set<Vehicle> fittedVehicles = new HashSet<>();
}