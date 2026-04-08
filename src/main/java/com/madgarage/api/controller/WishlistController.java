package com.madgarage.api.controller;

import com.madgarage.api.dto.WishlistItemResponse;
import com.madgarage.api.model.User;
import com.madgarage.api.services.UserService;
import com.madgarage.api.services.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * WishlistController is a thin HTTP routing layer.
 * All business logic lives in WishlistService.
 */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    /** GET /api/wishlist — returns the authenticated user's wishlist */
    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(wishlistService.getWishlist(user.getId()));
    }

    /** POST /api/wishlist/{productId} — toggles wishlist state for a product */
    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            Principal principal,
            @PathVariable Long productId) {
        User user = userService.getCurrentUser(principal.getName());
        boolean added = wishlistService.toggleWishlist(user, productId);
        return ResponseEntity.ok(Map.of(
                "added", added,
                "message", added ? "Added to wishlist" : "Removed from wishlist"
        ));
    }

    /** DELETE /api/wishlist/{productId} — removes a product from the wishlist */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(
            Principal principal,
            @PathVariable Long productId) {
        User user = userService.getCurrentUser(principal.getName());
        wishlistService.removeFromWishlist(user.getId(), productId);
        return ResponseEntity.ok("Removed from wishlist");
    }

    /** GET /api/wishlist/{productId}/check — checks if a product is in the wishlist */
    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            Principal principal,
            @PathVariable Long productId) {
        User user = userService.getCurrentUser(principal.getName());
        boolean inWishlist = wishlistService.isInWishlist(user.getId(), productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }
}
