package com.example.demo.repository;

import com.example.demo.domain.entity.Order;
import com.example.demo.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // Revenue analytics
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :from AND :to
        """)
    BigDecimal getTotalRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT DATE(o.createdAt) as date, SUM(o.totalAmount) as revenue, COUNT(o) as orderCount
        FROM Order o
        WHERE o.status = 'COMPLETED'
        AND o.createdAt BETWEEN :from AND :to
        GROUP BY DATE(o.createdAt)
        ORDER BY DATE(o.createdAt)
        """)
    List<Object[]> getDailyRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
}
