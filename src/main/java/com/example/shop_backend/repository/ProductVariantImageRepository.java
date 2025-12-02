package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductVariantImage;

@Repository
public interface ProductVariantImageRepository extends JpaRepository<ProductVariantImage, Integer> {
    List<ProductVariantImage> findByProductVariantId(Integer productVariantId);
    
    // Batch load variant images
    @Query("SELECT pvi FROM ProductVariantImage pvi WHERE pvi.productVariant.id IN :variantIds")
    List<ProductVariantImage> findByProductVariantIdIn(@Param("variantIds") List<Integer> variantIds);
    
    void deleteByProductVariantId(Integer productVariantId);
}
