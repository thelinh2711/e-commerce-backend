package com.example.shop_backend.repository;

import com.example.shop_backend.model.Order;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findByUserWithPayment(@Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.id = :id")
    Optional<Order> findByIdWithPayment(@Param("id") Integer id);
}
