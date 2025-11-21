package com.example.shop_backend.repository;

import com.example.shop_backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Kiểm tra xem user đã review variant này trong order chưa
    boolean existsByUserIdAndProductVariantIdAndOrderId(Integer userId, Integer productVariantId, Integer orderId);

    // Lấy danh sách review theo productId
    Page<Review> findByProductIdOrderByCreatedAtDesc(Integer productId, Pageable pageable);

    // Lọc theo productId và rating
    Page<Review> findByProductIdAndRatingOrderByCreatedAtDesc(Integer productId, Integer rating, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Integer productId);

    @Query("SELECT r.productVariant.id FROM Review r " +
            "WHERE r.user.id = :userId AND r.order.id = :orderId")
    List<Integer> findReviewedVariantIdsByUserAndOrder(@Param("userId") Integer userId,
                                                       @Param("orderId") Integer orderId);

    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
