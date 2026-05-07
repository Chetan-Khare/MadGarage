package com.madgarage.api.controller;

import com.madgarage.api.model.Coupon;
import com.madgarage.api.model.User;
import com.madgarage.api.services.CouponService;
import com.madgarage.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserService userService;

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAvailableCoupons(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(couponService.getAvailableCoupons(user));
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> validateCoupon(Principal principal, @RequestParam String code, @RequestParam Double amount) {
        User user = userService.getCurrentUser(principal.getName());
        Coupon coupon = couponService.validateCoupon(code, user, amount);
        Double discount = couponService.calculateDiscount(coupon, amount);
        
        return ResponseEntity.ok(Map.of(
            "code", coupon.getCode(),
            "description", coupon.getDescription(),
            "discountAmount", discount,
            "message", "Coupon applied successfully! You saved ₹" + discount
        ));
    }

    // Admin Endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.updateCoupon(id, coupon));
    }
}
