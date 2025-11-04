package com.example.shop_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shop_backend.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
}
