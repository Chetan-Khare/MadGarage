package com.madgarage.api.services;

import com.madgarage.api.dto.GarageProductDTO;
import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.model.Product;
import com.madgarage.api.model.ProductImage;
import com.madgarage.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final PricingService pricingService;

    // -----------------------------------------------------------------------
    // Queries
    // -----------------------------------------------------------------------

    private List<Product> fetchFilteredProducts(String category, Long vehicleId) {
        if (vehicleId != null) {
            if (category != null && !category.isEmpty()) {
                return productRepository.findByCategoryAndFittedVehiclesId(category, vehicleId);
            } else {
                return productRepository.findByFittedVehiclesId(vehicleId);
            }
        } else if (category != null && !category.isEmpty()) {
            try {
                FitmentCategory fitment = FitmentCategory.valueOf(category.toUpperCase());
                return productRepository.findByFitmentCategory(fitment);
            } catch (IllegalArgumentException e) {
                return productRepository.findAll();
            }
        } else {
            return productRepository.findAll();
        }
    }

    /**
     * Returns all products, optionally filtered by fitment category.
     */
    @Cacheable(value = "products", key = "{#category, #vehicleId}")
    public List<ProductResponse> getAllProducts(String category, Long vehicleId) {
        return fetchFilteredProducts(category, vehicleId).stream()
                .filter(product -> product.isActive() && !product.isFlagged())
                .map(product -> mapToResponse(product))
                .collect(Collectors.toList());
    }

    /**
     * Returns a single product by ID.
     */
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Part not detected in our warehouse inventory"));
    }

    /**
     * Returns all products with garage-discounted pricing using the tiered
     * discount system, optionally filtered by category.
     */
    @Cacheable(value = "products_garage", key = "{#category, #vehicleId}")
    public List<GarageProductDTO> getGarageProducts(String category, Long vehicleId) {
        return fetchFilteredProducts(category, vehicleId).stream()
                .filter(product -> product.isActive() && !product.isFlagged())
                .map(product -> {
            GarageProductDTO dto = new GarageProductDTO();
            dto.setId(product.getId());
            dto.setPartName(product.getPartName());
            dto.setImageUrl(product.getImageUrl());
            dto.setOriginalPrice(product.getPrice());
            dto.setGaragePrice(pricingService.calculateGaragePrice(product));
            dto.setCondition(product.getCondition() != null ? product.getCondition().name() : "NEW");
            dto.setCategory(product.getCategory());
            dto.setBrand(product.getBrand());
            dto.setColor(product.getColor());
            dto.setStockQuantity(product.getStockQuantity());
            dto.setWholesale(product.isWholesale());
            dto.setMrp(product.getMrp());
            dto.setDiscountPercentage(product.getDiscountPercentage());
            dto.setImageUrls(product.getImages() != null
                    ? product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                    : java.util.Collections.emptyList());
            dto.setRating(product.isManualRatingOverride() ? product.getManualRating() : 4.8);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Returns all products as DTOs (used by admin inventory view).
     */
    public List<ProductResponse> getAllProductsAsDto() {
        return productRepository.findAll().stream()
                .map(product -> mapToResponse(product))
                .collect(Collectors.toList());
    }

    // -----------------------------------------------------------------------
    // Admin Actions
    // -----------------------------------------------------------------------

    /**
     * Updates an existing product.
     */
    @CacheEvict(value = {"products", "products_garage"}, allEntries = true)
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
        if (request.getMrp() != null)
            product.setMrp(request.getMrp());
        if (request.getDiscountPercentage() != null)
            product.setDiscountPercentage(request.getDiscountPercentage());
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

        product.setManualRatingOverride(request.isManualRating());
        product.setManualRating(request.getRating());
        
        if (request.getFlagged() != null) product.setFlagged(request.getFlagged());
        if (request.getFlagReason() != null) product.setFlagReason(request.getFlagReason());
        if (request.getSellerResponse() != null) product.setSellerResponse(request.getSellerResponse());
        if (request.getWholesale() != null) product.setWholesale(request.getWholesale());

        productRepository.save(product);
    }

    /**
     * Toggles the flagged status of a product.
     *
     * @param id     the product ID
     * @param reason optional admin-provided reason for flagging (not yet persisted;
     *               add a flagReason field to Product to store it)
     */
    @CacheEvict(value = {"products", "products_garage"}, allEntries = true)
    public boolean toggleProductFlag(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));
        product.setFlagged(!product.isFlagged());
        product.setFlagReason(reason);
        productRepository.save(product);
        return product.isFlagged();
    }

    /**
     * Deletes a product.
     */
    @CacheEvict(value = {"products", "products_garage"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Product not found"));
        product.setActive(false);
        productRepository.save(product);
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
                .mrp(product.getMrp())
                .discountPercentage(product.getDiscountPercentage())
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
                .isManualRating(product.isManualRatingOverride())
                .rating(product.isManualRatingOverride() ? product.getManualRating() : 4.8)
                .wholesale(product.isWholesale())
                .active(product.isActive())
                .build();
    }
}
