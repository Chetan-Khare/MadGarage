package com.madgarage.api.controller;

import com.madgarage.api.dto.GarageProductDTO;
import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.services.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController is a thin HTTP routing layer.
 * All business logic lives in ProductService.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(@RequestParam(required = false) String category,
                                                @RequestParam(required = false) Long vehicleId) {
        return productService.getAllProducts(category, vehicleId);
    }

    @GetMapping("/garage")
    public List<GarageProductDTO> getGarageProducts(@RequestParam(required = false) String category,
                                                    @RequestParam(required = false) Long vehicleId) {
        return productService.getGarageProducts(category, vehicleId);
    }
}