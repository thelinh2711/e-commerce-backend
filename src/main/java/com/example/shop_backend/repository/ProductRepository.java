package com.example.shop_backend.repository;

import java.util.List;

import com.example.shop_backend.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAll();
    boolean existsByBrand(Brand brand);
}
