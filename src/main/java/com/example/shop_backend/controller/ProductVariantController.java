package com.example.shop_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.shop_backend.dto.request.CreateProductVariantRequest;
import com.example.shop_backend.dto.request.UpdateProductVariantRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ProductVariantResponse;
import com.example.shop_backend.service.ProductVariantService;

import jakarta.validation.Valid;

/**
 * REST Controller for Product Variant Management
 * 
 * Endpoints:
 * - GET    /api/product-variants/{id}           - Get variant by ID (PUBLIC)
 * - GET    /api/product-variants/product/{id}   - Get all variants of a product (PUBLIC)
 * - POST   /api/product-variants                - Create new variant (ADMIN only)
 * - PUT    /api/product-variants/{id}           - Update variant (color, size, stock, images) (ADMIN only)
 * - DELETE /api/product-variants/{id}           - Delete variant (ADMIN only)
 * 
 * All endpoints return data in ApiResponse wrapper format
 */
@RestController
@RequestMapping("/api/product-variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getVariantById(@PathVariable Integer id) {
        ProductVariantResponse variant = productVariantService.getVariantById(id);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .code(1000)
                .message("Success")
                .result(variant)
                .build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsByProductId(
            @PathVariable Integer productId) {
        List<ProductVariantResponse> variants = productVariantService.getVariantsByProductId(productId);
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantResponse>>builder()
                .code(1000)
                .message("Success")
                .result(variants)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createVariant(
            @Valid @RequestBody CreateProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductVariantResponse>builder()
                        .code(1000)
                        .message("Product variant created successfully")
                        .result(variant)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateVariant(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.updateVariant(id, request);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .code(1000)
                .message("Variant updated successfully")
                .result(variant)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Integer id) {
        productVariantService.deleteVariant(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(1000)
                .message("Variant deleted successfully")
                .build());
    }
}
