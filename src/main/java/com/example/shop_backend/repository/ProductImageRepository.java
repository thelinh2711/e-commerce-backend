package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductId(Integer productId);
    
    // Batch load images cho nhiều products
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds")
    List<ProductImage> findByProductIdIn(@Param("productIds") List<Integer> productIds);
}
