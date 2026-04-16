package com.madgarage.api.controller;

import com.madgarage.api.dto.AdminAnalyticsResponse;
import com.madgarage.api.dto.B2BUserRequest;
import com.madgarage.api.dto.OrderResponse;
import com.madgarage.api.dto.ProductResponse;
import com.madgarage.api.dto.UserProfileResponse;
import com.madgarage.api.services.OrderService;
import com.madgarage.api.services.ProductService;
import com.madgarage.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController is a thin HTTP routing layer.
 * All business logic lives in UserService, OrderService, and ProductService.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    public AdminController(UserService userService, OrderService orderService, ProductService productService) {
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(userService.getAdminAnalytics());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createB2BUser(@Valid @RequestBody B2BUserRequest request) {
        userService.createB2BUser(request);
        return ResponseEntity.ok(request.getRole() + " Account created successfully!");
    }

    @PostMapping("/users/{id}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok("Account successfully restored to active status.");
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody B2BUserRequest request) {
        userService.updateUserByAdmin(id, request);
        return ResponseEntity.ok("User record updated successfully!");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersProfileResponses());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User successfully deactivated/banned.");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersAsDto());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Validation check is now handled centrally in the OrderService implementation
        // but we ensure clean input here as well if necessary.
        return ResponseEntity.ok(orderService.updateOrderStatus(id, newStatus));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order successfully deleted.");
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<ProductResponse>> getInventory() {
        return ResponseEntity.ok(productService.getAllProductsAsDto());
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
            @Valid @RequestBody com.madgarage.api.dto.ProductRequest request) {
        productService.updateProduct(id, request);
        return ResponseEntity.ok("Product updated successfully!");
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    @PutMapping("/inventory/{id}/toggle-flag")
    public ResponseEntity<?> toggleProductFlag(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String reason = (body != null) ? body.get("reason") : null;
        boolean newState = productService.toggleProductFlag(id, reason);
        return ResponseEntity.ok("Product " + (newState ? "flagged" : "unflagged") + " successfully!");
    }
}
