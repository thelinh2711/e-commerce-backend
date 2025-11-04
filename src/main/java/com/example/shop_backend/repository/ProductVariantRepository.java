package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    @Query("SELECT pv FROM ProductVariant pv " +
           "LEFT JOIN FETCH pv.color " +
           "LEFT JOIN FETCH pv.size " +
           "WHERE pv.product.id = :productId")
    List<ProductVariant> findByProductIdWithColorAndSize(@Param("productId") Integer productId);
    
    List<ProductVariant> findByProductId(Integer productId);
}
