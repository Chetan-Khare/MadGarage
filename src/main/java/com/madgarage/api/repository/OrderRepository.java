package com.madgarage.api.repository;

import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import com.madgarage.api.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT SUM(grand_total) FROM orders WHERE status NOT IN ('PENDING_PAYMENT', 'CANCELLED', 'RETURNED', 'REFUNDED')", nativeQuery = true)
    Double calculateTotalRevenue();

    @Query("SELECT o FROM Order o " +
           "JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "LEFT JOIN FETCH p.seller " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);

    // ARCH-03 FIX: Single query loads orders + items + products for a user.
    // Eliminates the N+1 pattern where 20 orders caused 20+ extra queries.
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.user " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product p " +
           "LEFT JOIN FETCH p.seller " +
           "WHERE o.user = :user " +
           "ORDER BY o.id DESC")
    List<Order> findByUserWithItems(@Param("user") User user);

    @Query("SELECT o FROM Order o JOIN FETCH o.user LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product p LEFT JOIN FETCH p.seller WHERE o.fittingGarageId = :garageId AND o.status <> 'PENDING_PAYMENT' ORDER BY o.orderDate DESC")
    List<Order> findByFittingGarageIdWithItems(@Param("garageId") Long garageId);

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.user " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.product p " +
           "LEFT JOIN FETCH p.seller " +
           "WHERE o.status <> 'PENDING_PAYMENT' " +
           "ORDER BY o.id DESC")
    List<Order> findAllWithItems();

    @Query("SELECT SUM(oi.priceAtPurchase * oi.quantity) FROM OrderItem oi " +
           "WHERE oi.product.seller = :seller AND oi.order.status NOT IN (com.madgarage.api.enums.OrderStatus.PENDING_PAYMENT, com.madgarage.api.enums.OrderStatus.CANCELLED, com.madgarage.api.enums.OrderStatus.RETURNED, com.madgarage.api.enums.OrderStatus.REFUNDED)")
    Double calculateRevenueBySeller(@Param("seller") User seller);

    @Query(value = "SELECT SUM(grand_total) as total, " +
           "DATE_FORMAT(order_date, '%Y-%m') as month " +
           "FROM orders " +
           "WHERE order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
           "AND status NOT IN ('PENDING_PAYMENT', 'CANCELLED', 'RETURNED', 'REFUNDED') " +
           "GROUP BY month " +
           "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> getMonthlyRevenueForLastSixMonths();

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.user " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product p " +
           "LEFT JOIN FETCH p.seller " +
           "WHERE p.seller = :seller " +
           "AND o.status <> 'PENDING_PAYMENT' " +
           "ORDER BY o.id DESC")
    List<Order> findAllBySeller(@Param("seller") User seller);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Long id);

    List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime cutoff);
}