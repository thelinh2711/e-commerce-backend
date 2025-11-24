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
    
    // Lấy variants theo productId và filter active
    @Query("SELECT pv FROM ProductVariant pv " +
           "LEFT JOIN FETCH pv.color " +
           "LEFT JOIN FETCH pv.size " +
           "WHERE pv.product.id = :productId AND pv.active = :active")
    List<ProductVariant> findByProductIdAndActive(@Param("productId") Integer productId, 
                                                   @Param("active") Boolean active);
    
    List<ProductVariant> findByProductId(Integer productId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId "
        + "AND ((:colorId IS NULL AND pv.color IS NULL) OR pv.color.id = :colorId) "
        + "AND ((:sizeId IS NULL AND pv.size IS NULL) OR pv.size.id = :sizeId)")
    java.util.Optional<ProductVariant> findDuplicate(@Param("productId") Integer productId,
                            @Param("colorId") Integer colorId,
                            @Param("sizeId") Integer sizeId);
}