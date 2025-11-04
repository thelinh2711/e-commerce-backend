package com.example.shop_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {
}
