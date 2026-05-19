package com.madgarage.api.repository;

import com.madgarage.api.model.OrderRating;
import com.madgarage.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRatingRepository extends JpaRepository<OrderRating, Long> {
    Optional<OrderRating> findByOrderId(Long orderId);
    List<OrderRating> findAllByOrderIdIn(List<Long> orderIds);
    boolean existsByOrderId(Long orderId);
}
