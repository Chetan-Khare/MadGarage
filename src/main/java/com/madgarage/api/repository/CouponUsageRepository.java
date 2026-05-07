package com.madgarage.api.repository;

import com.madgarage.api.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    long countByCouponIdAndUserId(Long couponId, Long userId);
    Optional<CouponUsage> findByCouponIdAndOrderId(Long couponId, Long orderId);
    void deleteByCouponIdAndOrderId(Long couponId, Long orderId);
}
