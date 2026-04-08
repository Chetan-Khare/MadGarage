package com.madgarage.api.services;

import com.madgarage.api.dto.GarageProductDTO;
import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.model.Product;
import com.madgarage.api.model.ProductImage;
import com.madgarage.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductService owns all product query and mapping business logic.
 * No controller should call ProductRepository directly — route through this
 * service.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    /**
     * Returns all products, optionally filtered by fitment category.
     */
    public List<ProductResponse> getAllProducts(String category, Long vehicleId) {
        List<Product> products;
        if (vehicleId != null) {
            if (category != null && !category.isEmpty()) {
                products = productRepository.findByCategoryAndFittedVehiclesId(category, vehicleId);
            } else {
                products = productRepository.findByFittedVehiclesId(vehicleId);
            }
        } else if (category != null && !category.isEmpty()) {
            try {
                FitmentCategory fitment = FitmentCategory.valueOf(category.toUpperCase());
                products = productRepository.findByFitmentCategory(fitment);
            } catch (IllegalArgumentException e) {
                products = productRepository.findAll();
            }
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Returns all products with garage-discounted pricing (5% off), optionally
     * filtered by category.
     */
    public List<GarageProductDTO> getGarageProducts(String category, Long vehicleId) {
        List<Product> products;
        if (vehicleId != null) {
            if (category != null && !category.isEmpty()) {
                products = productRepository.findByCategoryAndFittedVehiclesId(category, vehicleId);
            } else {
                products = productRepository.findByFittedVehiclesId(vehicleId);
            }
        } else if (category != null && !category.isEmpty()) {
            try {
                FitmentCategory fitment = FitmentCategory.valueOf(category.toUpperCase());
                products = productRepository.findByFitmentCategory(fitment);
            } catch (IllegalArgumentException e) {
                products = productRepository.findAll();
            }
        } else {
            products = productRepository.findAll();
        }

        return products.stream().map(product -> {
            GarageProductDTO dto = new GarageProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getPartName());
            dto.setImageUrl(product.getImageUrl());
            dto.setOriginalPrice(product.getPrice());
            double discounted = product.getPrice() * 0.95;
            dto.setGaragePrice(Math.round(discounted * 100.0) / 100.0);
            dto.setCondition(product.getCondition() != null ? product.getCondition().name() : "NEW");
            dto.setCategory(product.getCategory());
            dto.setColor(product.getColor());
            dto.setStockQuantity(product.getStockQuantity());
            dto.setImageUrls(product.getImages() != null
                    ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                    : java.util.Collections.emptyList());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Returns all products as DTOs (used by admin inventory view).
     */
    public List<ProductResponse> getAllProductsAsDto() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -----------------------------------------------------------------------
    // Admin Actions
    // -----------------------------------------------------------------------

    /**
     * Updates an existing product.
     */
    public void updateProduct(Long id, com.madgarage.api.dto.ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));

        if (request.getSku() != null)
            product.setSku(request.getSku());
        if (request.getBrand() != null)
            product.setBrand(request.getBrand());
        if (request.getPartName() != null)
            product.setPartName(request.getPartName());
        if (request.getCategory() != null)
            product.setCategory(request.getCategory());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getImageUrl() != null)
            product.setImageUrl(request.getImageUrl());
        if (request.getColor() != null)
            product.setColor(request.getColor());
        if (request.getStockQuantity() != null)
            product.setStockQuantity(request.getStockQuantity());
        if (request.getFitmentCategory() != null)
            product.setFitmentCategory(request.getFitmentCategory());
        if (request.getCondition() != null)
            product.setCondition(request.getCondition());
        if (request.getInstallationGuideUrl() != null)
            product.setInstallationGuideUrl(request.getInstallationGuideUrl());

        productRepository.save(product);
    }

    /**
     * Toggles the flagged status of a product.
     */
    public boolean toggleProductFlag(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));
        product.setFlagged(!product.isFlagged());
        if (product.isFlagged()) {
            product.setFlagReason(reason);
        } else {
            product.setFlagReason(null);
        }
        productRepository.save(product);
        return product.isFlagged();
    }

    /**
     * Deletes a product.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    // -----------------------------------------------------------------------
    // Mapping
    // -----------------------------------------------------------------------

    /**
     * Maps a Product entity to a safe, outbound ProductResponse DTO.
     */
    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .brand(product.getBrand())
                .partName(product.getPartName())
                .category(product.getCategory())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .color(product.getColor())
                .stockQuantity(product.getStockQuantity())
                .fitmentCategory(product.getFitmentCategory())
                .condition(product.getCondition())
                .flagged(product.isFlagged())
                .flagReason(product.getFlagReason())
                .sellerResponse(product.getSellerResponse())
                .installationGuideUrl(product.getInstallationGuideUrl())
                .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
                .imageUrls(product.getImages() != null
                        ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                        : java.util.Collections.emptyList())
                .build();
    }
}
