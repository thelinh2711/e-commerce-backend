package com.example.shop_backend.controller;

import java.util.List;

import com.example.shop_backend.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
// .\mvnw.cmd spring-boot:run
/**
 * ProductController - REST API endpoints cho quản lý sản phẩm
 * 
 * Phân quyền:
 * - GET: PUBLIC (tất cả user đều xem được, không có costPrice)
 * - GET (OWNER): Xem đầy đủ bao gồm costPrice
 * - POST (ADMIN): Tạo sản phẩm, không có costPrice (nếu gửi costPrice sẽ bị ignore)
 * - POST (OWNER): Tạo sản phẩm, có thể thêm costPrice
 * - PUT (ADMIN): Cập nhật tất cả các trường trừ costPrice (gửi costPrice → lỗi 403)
 * - PUT (OWNER): Cập nhật bao gồm cả costPrice
 * - DELETE: ADMIN/OWNER
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lấy danh sách sản phẩm
     * - Nếu user là OWNER: Trả về có costPrice
     * - Nếu không (PUBLIC/ADMIN): Không có costPrice
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @AuthenticationPrincipal User currentUser) {
        List<ProductResponse> products = productService.getAllProducts(currentUser);
        
        ProductListResponse response = ProductListResponse.builder()
                .success(true)
                .data(products)
                .build();
        System.out.println("Current user: " + currentUser.getEmail() + ", role: " + currentUser.getRole());
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết sản phẩm
     * - Nếu user là OWNER: Trả về đầy đủ bao gồm costPrice
     * - Nếu không: Không có costPrice
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.getProductById(id, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Lấy sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo sản phẩm mới
     * - ADMIN: Không có costPrice trong request (bị ignore nếu có)
     * - OWNER: Có thể thêm costPrice trong request
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.createProduct(request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Tạo sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cập nhật sản phẩm
     * - Request chứa field nào thì cập nhật field đó
     * - ADMIN: CẤM gửi cost_price trong request (sẽ báo lỗi 403)
     * - OWNER: Được phép cập nhật tất cả bao gồm cost_price
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        ProductResponse product = productService.updateProduct(id, request, currentUser);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .code(1000)
                .message("Cập nhật sản phẩm thành công")
                .result(product)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa sản phẩm (ADMIN/OWNER)
     */
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