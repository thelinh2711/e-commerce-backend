package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductVariantImage;

@Repository
public interface ProductVariantImageRepository extends JpaRepository<ProductVariantImage, Integer> {
    List<ProductVariantImage> findByProductVariantId(Integer productVariantId);
}
