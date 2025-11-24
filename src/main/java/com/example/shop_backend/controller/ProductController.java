package com.example.shop_backend.controller;

import java.util.List;

import com.example.shop_backend.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.request.CreateProductRequest;
import com.example.shop_backend.dto.request.UpdateProductRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ProductListResponse;
import com.example.shop_backend.dto.response.ProductResponse;
import com.example.shop_backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * GET /api/products?active=true
     * GET /api/products?active=false
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(required = false) Boolean active,
            @AuthenticationPrincipal User currentUser) {
        
        List<ProductResponse> products = productService.getAllProducts(active, currentUser);
        
        ProductListResponse response = ProductListResponse.builder()
                .success(true)
                .data(products)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products/{id}
     * GET /api/products/{id}?active=true
     * GET /api/products/{id}?active=false
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Integer id,
            @RequestParam(required = false) Boolean active,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.getProductById(id, active, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Lấy sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @ModelAttribute CreateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.createProduct(request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Tạo sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.updateProduct(id, request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Cập nhật sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa sản phẩm thành công")
                .build();
        
        return ResponseEntity.ok(response);
    }
}