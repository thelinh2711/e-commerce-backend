package com.example.shop_backend.repository;

import com.example.shop_backend.model.Wishlist;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    List<Wishlist> findAllByUser(User user);

    // Đếm số lượt yêu thích của 1 sản phẩm
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.product.id = :productId")
    long countLikesByProductId(Integer productId);

    // Danh sách sản phẩm theo lượt thích giảm dần
    @Query("SELECT w.product.id, COUNT(w) as likeCount FROM Wishlist w GROUP BY w.product.id HAVING COUNT(w) > 0 ORDER BY likeCount DESC")
    List<Object[]> findProductsByLikeCountDesc();
}
