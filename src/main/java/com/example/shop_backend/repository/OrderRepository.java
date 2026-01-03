package com.example.shop_backend.repository;

import com.example.shop_backend.dto.response.OrderListResponse;
import com.example.shop_backend.model.Order;
import com.example.shop_backend.model.ProductVariant;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findByUserWithPayment(@Param("user") User user);

    @Query("""
    SELECT DISTINCT o FROM Order o
    LEFT JOIN FETCH o.payment
    LEFT JOIN FETCH o.items oi
    LEFT JOIN FETCH oi.productVariant pv
    LEFT JOIN FETCH pv.product
    LEFT JOIN FETCH pv.color
    LEFT JOIN FETCH pv.size
    WHERE o.id = :id
    """)
    Optional<Order> findByIdWithPayment(@Param("id") Integer id);

    @Query("""
    SELECT DISTINCT pv FROM ProductVariant pv
    LEFT JOIN FETCH pv.images
    WHERE pv.id IN :variantIds
    """)
    List<ProductVariant> findVariantsWithImages(@Param("variantIds") List<Integer> variantIds);

    @Query("""
        SELECT new com.example.shop_backend.dto.response.OrderListResponse(
            o.id,
            o.fullName,
            o.phone,
            o.status,
            o.totalAmount,
            p.paymentMethod,
            p.status,
            o.createdAt
        )
        FROM Order o
        LEFT JOIN o.payment p
        WHERE (:keyword IS NULL 
               OR CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%')
               OR LOWER(o.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR o.phone LIKE CONCAT('%', :keyword, '%'))
          AND (:status IS NULL OR o.status = :status)
          AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
          AND (:toDate IS NULL OR o.createdAt <= :toDate)
        ORDER BY o.createdAt DESC
        """)
    Page<OrderListResponse> searchOrderList(
            String keyword,
            OrderStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    );

    @Query("""
    SELECT 
        COUNT(o),
        SUM(CASE WHEN o.status = 'PENDING' THEN 1 ELSE 0 END),
        SUM(CASE WHEN o.status = 'CONFIRMED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN o.status = 'SHIPPED' THEN 1 ELSE 0 END),
        SUM(CASE WHEN o.status = 'DELIVERED' THEN 1 ELSE 0 END)
    FROM Order o
    WHERE (:from IS NULL OR o.createdAt >= :from)
      AND (:to IS NULL OR o.createdAt <= :to)
""")
    Object countOrderDashboardStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

}
