package com.madgarage.api.controller;

import com.madgarage.api.dto.Base64ProductRequest;
import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.dto.SellerAnalyticsResponse;
import com.madgarage.api.model.User;
import com.madgarage.api.services.InventoryService;
import com.madgarage.api.services.ProductCreationService;
import com.madgarage.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/**
 * InventoryController is a thin HTTP routing layer.
 * All business logic lives in InventoryService, ProductCreationService and UserService.
 */
@RestController
@RequestMapping("/api/seller/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'WORKER')")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductCreationService productCreationService;
    private final UserService userService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addProductWithImageAndFitment(
            Principal principal,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam(value = "guide", required = false) MultipartFile guide,
            @RequestParam("sku") String sku,
            @RequestParam("brand") String brand,
            @RequestParam("partName") String partName,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam("condition") String condition,
            @RequestParam("fitmentCategory") String fitmentCategory,
            @RequestParam(value = "vehicleIds", required = false) List<Long> vehicleIds) {

        User seller = userService.getCurrentUser(principal.getName());
        productCreationService.addProduct(seller, images, guide, sku, brand, partName,
                category, price, description, stockQuantity, color,
                condition, fitmentCategory, vehicleIds != null ? vehicleIds : java.util.Collections.emptyList());
        return ResponseEntity.ok("Product and Guide saved successfully!");
    }

    @PostMapping("/base64")
    public ResponseEntity<?> addProductBase64(
            Principal principal,
            @RequestBody Base64ProductRequest request) {
        
        User seller = userService.getCurrentUser(principal.getName());
        productCreationService.addProductBase64(seller, request);
        return ResponseEntity.ok("Product listed successfully via Base64!");
    }

    @PutMapping("/{id}/base64")
    public ResponseEntity<?> updateProductBase64(
            Principal principal,
            @PathVariable Long id,
            @RequestBody Base64ProductRequest request) {
        
        User seller = userService.getCurrentUser(principal.getName());
        productCreationService.updateProductBase64(seller, id, request);
        return ResponseEntity.ok("Product updated successfully via Base64!");
    }

    @GetMapping("/analytics")
    public ResponseEntity<SellerAnalyticsResponse> getSellerAnalytics(Principal principal) {
        User seller = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(inventoryService.getSellerAnalytics(seller));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getSellerInventory(Principal principal) {
        User seller = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(inventoryService.getSellerProducts(seller));
    }

    /**
     * DELETE /api/seller/inventory/{id}
     * Sellers can only delete their OWN products.
     * Returns 403 if the product belongs to a different seller.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSellerProduct(@PathVariable Long id, Principal principal) {
        User seller = userService.getCurrentUser(principal.getName());
        boolean deleted = productCreationService.deleteSellerProduct(seller, id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to delete this product.");
        }
        return ResponseEntity.ok("Product deleted successfully.");
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<?> respondToFlag(@PathVariable Long id, Principal principal, @RequestBody java.util.Map<String, String> body) {
        User seller = userService.getCurrentUser(principal.getName());
        String response = body.get("response");
        boolean success = productCreationService.addSellerResponse(seller, id, response);
        if (!success) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to respond to this product.");
        }
        return ResponseEntity.ok("Response recorded and sent to the administration.");
    }
}