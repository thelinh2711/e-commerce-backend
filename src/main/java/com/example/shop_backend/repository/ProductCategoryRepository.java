package com.example.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    
    // Tối ưu: Fetch category cùng lúc để tránh N+1 query
    @Query("SELECT pc FROM ProductCategory pc LEFT JOIN FETCH pc.category WHERE pc.product.id = :productId")
    List<ProductCategory> findByProductIdWithCategory(@Param("productId") Integer productId);
    
    // Batch load categories cho nhiều products
    @Query("SELECT pc FROM ProductCategory pc LEFT JOIN FETCH pc.category WHERE pc.product.id IN :productIds")
    List<ProductCategory> findByProductIdInWithCategory(@Param("productIds") List<Integer> productIds);
    
    List<ProductCategory> findByProductId(Integer productId);

    boolean existsByCategory_Id(Integer categoryId);

    void deleteByCategory_Id(Integer categoryId);

    @Query("SELECT COUNT(pc) FROM ProductCategory pc WHERE pc.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Integer categoryId);
}
