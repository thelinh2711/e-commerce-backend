package com.example.shop_backend.repository;

import com.example.shop_backend.model.Order;
import com.example.shop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findByUserWithPayment(@Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.id = :id")
    Optional<Order> findByIdWithPayment(@Param("id") Integer id);
}
