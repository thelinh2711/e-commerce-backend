package com.example.shop_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shop_backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);
}
