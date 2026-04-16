package com.madgarage.api.repository;

import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT SUM(grand_total) FROM orders", nativeQuery = true)
    Double calculateTotalRevenue();

    // SEC-06 FIX: JOIN FETCH ensures user is loaded in the same query,
    // preventing LazyInitializationException that was causing the receipt 403.
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);

    // ARCH-03 FIX: Single query loads orders + items + products for a user.
    // Eliminates the N+1 pattern where 20 orders caused 20+ extra queries.
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.user " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product " +
           "WHERE o.user = :user " +
           "ORDER BY o.id DESC")
    List<Order> findByUserWithItems(@Param("user") User user);

    @Query("SELECT SUM(oi.priceAtPurchase * oi.quantity) FROM OrderItem oi " +
           "WHERE oi.product.seller = :seller")
    Double calculateRevenueBySeller(@Param("seller") User seller);

    @Query(value = "SELECT SUM(grand_total) as total, " +
           "DATE_FORMAT(order_date, '%Y-%m') as month " +
           "FROM orders " +
           "WHERE order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
           "GROUP BY month " +
           "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> getMonthlyRevenueForLastSixMonths();

    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN FETCH o.items i " +
           "JOIN FETCH i.product p " +
           "WHERE p.seller = :seller " +
           "ORDER BY o.id DESC")
    List<Order> findAllBySeller(@Param("seller") User seller);
}