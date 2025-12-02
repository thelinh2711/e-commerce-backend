package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductLabel;

@Repository
public interface ProductLabelRepository extends JpaRepository<ProductLabel, Integer> {
    @Query("SELECT pl FROM ProductLabel pl LEFT JOIN FETCH pl.label WHERE pl.product.id = :productId")
    List<ProductLabel> findByProductIdWithLabel(@Param("productId") Integer productId);
    
    // Batch load labels cho nhiều products
    @Query("SELECT pl FROM ProductLabel pl LEFT JOIN FETCH pl.label WHERE pl.product.id IN :productIds")
    List<ProductLabel> findByProductIdInWithLabel(@Param("productIds") List<Integer> productIds);
    
    List<ProductLabel> findByProductId(Integer productId);
}
