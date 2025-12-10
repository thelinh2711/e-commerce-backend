package com.example.shop_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shop_backend.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    /**
     * Đếm số lượng variants bán được của từng product
     * Ví dụ: 10 variants khác nhau của product A → sold = 10
     * Chỉ tính order đã DELIVERED
     */
    @Query("SELECT pv.product.id as productId, COUNT(DISTINCT pv.id) as totalSold " +
           "FROM OrderItem oi " +
           "JOIN oi.productVariant pv " +
           "JOIN oi.order o " +
           "WHERE o.status IN ('DELIVERED') " +
           "AND oi.productVariant.product.id IN :productIds " +
           "GROUP BY pv.product.id")
    List<Map<String, Object>> calculateTotalSoldByProductIds(@Param("productIds") List<Integer> productIds);
    
    /**
     * Đếm số lượng variants bán được theo product trong khoảng thời gian
     */
    @Query("SELECT pv.product.id as productId, COUNT(DISTINCT pv.id) as totalSold " +
           "FROM OrderItem oi " +
           "JOIN oi.productVariant pv " +
           "JOIN oi.order o " +
           "WHERE o.status IN ('DELIVERED') " +
           "AND oi.productVariant.product.id IN :productIds " +
           "AND o.createdAt >= :startDate " +
           "AND o.createdAt <= :endDate " +
           "GROUP BY pv.product.id")
    List<Map<String, Object>> calculateTotalSoldByProductIdsAndDateRange(
        @Param("productIds") List<Integer> productIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
