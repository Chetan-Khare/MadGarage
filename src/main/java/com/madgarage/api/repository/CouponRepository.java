package com.madgarage.api.repository;

import com.madgarage.api.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeIgnoreCase(String code);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE LOWER(c.code) = LOWER(:code)")
    Optional<Coupon> findByCodeIgnoreCaseWithLock(@org.springframework.data.repository.query.Param("code") String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND (c.startDate IS NULL OR c.startDate <= :now) " +
           "AND (c.endDate IS NULL OR c.endDate >= :now) " +
           "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);
}
