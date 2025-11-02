package com.example.shop_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_backend.dto.response.ProductListResponse;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Lấy danh sách tất cả sản phẩm
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        
        ProductListResponse response = ProductListResponse.builder()
                .success(true)
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
