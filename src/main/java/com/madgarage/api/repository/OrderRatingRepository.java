package com.madgarage.api.repository;

import com.madgarage.api.model.OrderRating;
import com.madgarage.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRatingRepository extends JpaRepository<OrderRating, Long> {
    Optional<OrderRating> findByOrderId(Long orderId);
    boolean existsByOrderId(Long orderId);
}
