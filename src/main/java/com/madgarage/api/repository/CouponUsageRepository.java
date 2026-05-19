package com.madgarage.api.repository;

import com.madgarage.api.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    long countByCouponIdAndUserId(Long couponId, Long userId);
    Optional<CouponUsage> findByCouponIdAndOrderId(Long couponId, Long orderId);
    void deleteByCouponIdAndOrderId(Long couponId, Long orderId);

    @org.springframework.data.jpa.repository.Query("SELECT cu.coupon.id, COUNT(cu) FROM CouponUsage cu WHERE cu.user.id = :userId AND cu.coupon.id IN :couponIds GROUP BY cu.coupon.id")
    List<Object[]> countUsagesByUserAndCouponIds(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("couponIds") List<Long> couponIds);
}
