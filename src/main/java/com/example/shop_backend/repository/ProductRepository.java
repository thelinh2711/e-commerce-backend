package com.example.shop_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.Brand;
import com.example.shop_backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Tối ưu: Fetch brand cùng lúc với product để tránh N+1 query
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT p FROM Product p")
    List<Product> findAllWithBrand();
    
    // Lọc theo active và fetch brand
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT p FROM Product p WHERE p.active = :active")
    List<Product> findByActiveWithBrand(@Param("active") Boolean active);
    
    // Tìm product theo ID với brand
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithBrand(@Param("id") Integer id);
    
    // Lấy 12 products bắt đầu từ id
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT p FROM Product p WHERE p.id >= :startId ORDER BY p.id ASC")
    List<Product> findTop12ByIdGreaterThanEqualWithBrand(@Param("startId") Integer startId, org.springframework.data.domain.Pageable pageable);
    
    // Lấy 12 products bắt đầu từ id và filter active
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT p FROM Product p WHERE p.id >= :startId AND p.active = :active ORDER BY p.id ASC")
    List<Product> findTop12ByIdGreaterThanEqualAndActiveWithBrand(@Param("startId") Integer startId, @Param("active") Boolean active, org.springframework.data.domain.Pageable pageable);
    
    boolean existsByBrand(Brand brand);
}