package com.madgarage.api.services;

import com.madgarage.api.dto.WishlistItemResponse;
import com.madgarage.api.model.Product;
import com.madgarage.api.model.User;
import com.madgarage.api.model.Wishlist;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    /**
     * Toggles wishlist status for a product. Returns true if added, false if removed.
     */
    @Transactional
    public boolean toggleWishlist(User user, Long productId) {
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return false;
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(wishlist);
            return true;
        }
    }

    /**
     * Gets all wishlisted items for a user.
     */
    public List<WishlistItemResponse> getWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a product is in a user's wishlist.
     */
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Removes a product from a user's wishlist.
     */
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse mapToResponse(Wishlist wishlist) {
        Product p = wishlist.getProduct();
        return WishlistItemResponse.builder()
                .productId(p.getId())
                .name(p.getPartName())
                .brand(p.getBrand())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .category(p.getCategory())
                .condition(p.getCondition() != null ? p.getCondition().name() : "NEW")
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}
