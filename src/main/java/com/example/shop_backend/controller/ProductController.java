package com.example.shop_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ProductListResponse;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ProductController - REST API endpoints cho quản lý sản phẩm
 * 
 * Base URL: /api/products
 * 
 * Endpoints:
 * - GET /api/products - Lấy danh sách sản phẩm (PUBLIC)
 * - GET /api/products/{id} - Lấy chi tiết 1 sản phẩm (PUBLIC)
 * - POST /api/products - Tạo sản phẩm mới (ADMIN only)
 * - PUT /api/products/{id} - Cập nhật sản phẩm (ADMIN only)
 * - DELETE /api/products/{id} - Xóa sản phẩm (ADMIN only)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
// .\mvnw.cmd spring-boot:run
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        
        ProductListResponse response = ProductListResponse.builder()
                .success(true)
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Integer id) {
        ProductResponse product = productService.getProductById(id);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Lấy sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        
        ProductResponse product = productService.createProduct(request);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Tạo sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        
        ProductResponse product = productService.updateProduct(id, request);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Cập nhật sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa sản phẩm thành công")
                .build();
        
        return ResponseEntity.ok(response);
    }
}
