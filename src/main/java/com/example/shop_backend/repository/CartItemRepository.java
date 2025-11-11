package com.example.shop_backend.repository;

import com.example.shop_backend.model.Cart;
import com.example.shop_backend.model.CartItem;
import com.example.shop_backend.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant variant);
    void deleteByCart(Cart cart);
}
