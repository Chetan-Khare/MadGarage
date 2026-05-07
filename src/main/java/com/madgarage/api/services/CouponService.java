package com.madgarage.api.services;

import com.madgarage.api.model.Coupon;
import com.madgarage.api.model.CouponUsage;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.CouponRepository;
import com.madgarage.api.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public List<Coupon> getAvailableCoupons(User user) {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> activeCoupons = couponRepository.findActiveCoupons(now);
        
        // Filter out coupons that the user has already maxed out
        return activeCoupons.stream()
                .filter(c -> couponUsageRepository.countByCouponIdAndUserId(c.getId(), user.getId()) < c.getMaxUsagePerUser())
                .collect(Collectors.toList());
    }

    public Coupon validateCoupon(String code, User user, Double orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon code not found."));

        if (!coupon.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This coupon is no longer active.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && coupon.getStartDate().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This coupon is not yet valid.");
        }
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This coupon has expired.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This coupon usage limit has been reached.");
        }

        if (couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), user.getId()) >= coupon.getMaxUsagePerUser()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already used this coupon.");
        }

        if (orderAmount < coupon.getMinOrderAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum order amount of ₹" + coupon.getMinOrderAmount() + " required to use this coupon.");
        }

        return coupon;
    }

    public Double calculateDiscount(Coupon coupon, Double orderAmount) {
        if (coupon.getDiscountType() == Coupon.CouponType.FIXED) {
            return Math.min(coupon.getDiscountAmount(), orderAmount);
        } else {
            Double discount = (orderAmount * coupon.getDiscountAmount()) / 100.0;
            if (coupon.getMaxDiscountAmount() != null) {
                discount = Math.min(discount, coupon.getMaxDiscountAmount());
            }
            return discount;
        }
    }

    @Transactional
    public void recordUsage(Coupon coupon, User user, Order order) {
        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .orderId(order.getId())
                .build();
        couponUsageRepository.save(usage);

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    @Transactional
    public void rollbackUsage(String code, Long orderId) {
        if (code == null) return;
        couponRepository.findByCodeIgnoreCase(code).ifPresent(coupon -> {
            couponUsageRepository.findByCouponIdAndOrderId(coupon.getId(), orderId).ifPresent(usage -> {
                couponUsageRepository.delete(usage);
                coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
                couponRepository.save(coupon);
            });
        });
    }
    
    // Admin CRUD
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
    
    public Coupon updateCoupon(Long id, Coupon details) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found."));
        coupon.setDescription(details.getDescription());
        coupon.setDiscountType(details.getDiscountType());
        coupon.setDiscountAmount(details.getDiscountAmount());
        coupon.setMinOrderAmount(details.getMinOrderAmount());
        coupon.setMaxDiscountAmount(details.getMaxDiscountAmount());
        coupon.setUsageLimit(details.getUsageLimit());
        coupon.setMaxUsagePerUser(details.getMaxUsagePerUser());
        coupon.setStartDate(details.getStartDate());
        coupon.setEndDate(details.getEndDate());
        coupon.setActive(details.isActive());
        return couponRepository.save(coupon);
    }
}
