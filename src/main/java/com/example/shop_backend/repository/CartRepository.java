package com.example.shop_backend.repository;

import com.example.shop_backend.model.Cart;
import com.example.shop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
}
