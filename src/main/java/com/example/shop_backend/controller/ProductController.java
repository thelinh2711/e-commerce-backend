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

    private final ProductService productService;

    /**
     * GET /api/products - Lấy danh sách tất cả sản phẩm
     * 
     * @return ResponseEntity<ProductListResponse>
     *         - success: true
     *         - data: List<ProductResponse>
     * 
     * Access: PUBLIC (không cần authentication)
     * 
     * Response format:
     * {
     *   "success": true,
     *   "data": [...]
     * }
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

    /**
     * GET /api/products/{id} - Lấy chi tiết 1 sản phẩm theo ID
     * 
     * @param id - ID của sản phẩm (path variable)
     * @return ResponseEntity<ApiResponse<ProductResponse>>
     *         - code: 1000 (success)
     *         - message: "Lấy sản phẩm thành công"
     *         - result: ProductResponse object
     * 
     * Access: PUBLIC (không cần authentication)
     * 
     * Response format:
     * {
     *   "code": 1000,
     *   "message": "Lấy sản phẩm thành công",
     *   "result": {...}
     * }
     */
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

    /**
     * POST /api/products - Thêm sản phẩm mới
     * 
     * @param request - CreateProductRequest (validated)
     *        Required fields:
     *        - name: Tên sản phẩm
     *        - description: Mô tả
     *        - price: Giá sản phẩm
     *        - brandId: ID thương hiệu
     *        - stock: Số lượng tồn kho
     *        Optional fields:
     *        - discountPercent: % giảm giá (default: 0)
     *        - categoryIds: Danh sách ID categories
     *        - labelIds: Danh sách ID labels
     *        - images: Danh sách ảnh sản phẩm
     *        - variants: Danh sách biến thể (size, color)
     * 
     * @return ResponseEntity<ApiResponse<ProductResponse>>
     *         - code: 1000 (success)
     *         - message: "Tạo sản phẩm thành công"
     *         - result: ProductResponse of created product
     *         - HTTP Status: 201 CREATED
     * 
     * Access: ADMIN role required (JWT token)
     * Authorization: Bearer <token>
     * 
     * Note:
     * - cost_price sẽ tự động set NULL
     * - Variant SKU tự động generate: "VAR-{productId}-{counter}"
     * - Ảnh đầu tiên tự động là thumbnail
     */
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

    /**
     * PUT /api/products/{id} - Cập nhật sản phẩm
     * 
     * @param id - ID sản phẩm cần update (path variable)
     * @param request - UpdateProductRequest (validated)
     *        Fields tương tự CreateProductRequest
     * 
     * @return ResponseEntity<ApiResponse<ProductResponse>>
     *         - code: 1000 (success)
     *         - message: "Cập nhật sản phẩm thành công"
     *         - result: ProductResponse of updated product
     *         - HTTP Status: 200 OK
     * 
     * Access: ADMIN role required (JWT token)
     * Authorization: Bearer <token>
     * 
     * Note:
     * - Xóa và tạo lại tất cả: categories, labels, images, variants
     * - Sử dụng flush() để tránh duplicate key error
     * - Giữ nguyên số lượng đã bán (sold items)
     */
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

    /**
     * DELETE /api/products/{id} - Xóa sản phẩm
     * 
     * @param id - ID sản phẩm cần xóa (path variable)
     * @return ResponseEntity<ApiResponse<Void>>
     *         - code: 1000 (success)
     *         - message: "Xóa sản phẩm thành công"
     *         - result: null
     *         - HTTP Status: 200 OK
     * 
     * Access: ADMIN role required (JWT token)
     * Authorization: Bearer <token>
     * 
     * Note:
     * - Xóa cascade: variant images -> variants -> images -> labels -> categories -> product
     * - Không thể khôi phục sau khi xóa
     */
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
