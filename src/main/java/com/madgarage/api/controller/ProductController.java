package com.madgarage.api.controller;

import com.madgarage.api.model.Product;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.dto.GarageProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. STANDARD CUSTOMERS (Full Price)
    // Accessed via: GET /api/products
    @GetMapping
    public List<Product> getAllProducts() {
        // This grabs all products from MySQL and sends them to your phone as JSON!
        return productRepository.findAll();
    }

    // 2. GARAGE OWNERS (5% Discount)
    // Accessed via: GET /api/products/garage
    @PreAuthorize("hasAuthority('GARAGE')") // Locks this route to only logged-in garages
    @GetMapping("/garage")
    public List<GarageProductDTO> getGarageProducts() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream().map(product -> {
            GarageProductDTO dto = new GarageProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getPartName());

            // Assuming your Product model has getPrice() and getImageUrl()
            // If they are named differently, update these method calls!
            dto.setImageUrl(product.getImageUrl());
            dto.setOriginalPrice(product.getPrice());

            // Calculate 5% discount (Multiply by 0.95) and round to 2 decimal places
            double discounted = product.getPrice() * 0.95;
            dto.setGaragePrice(Math.round(discounted * 100.0) / 100.0);

            return dto;
        }).collect(Collectors.toList());
    }
}