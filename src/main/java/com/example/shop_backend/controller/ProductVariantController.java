package com.example.shop_backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.request.CreateProductVariantRequest;
import com.example.shop_backend.dto.request.UpdateProductVariantRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ProductVariantResponse;
import com.example.shop_backend.service.ProductVariantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/product-variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;

    /**
     * GET /api/product-variants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getVariantById(
            @PathVariable Integer id) {
        
        ProductVariantResponse variant = productVariantService.getVariantById(id);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .code(1000)
                .message("Success")
                .result(variant)
                .build());
    }

    /**
     * GET /api/product-variants/product/{productId}
     * GET /api/product-variants/product/{productId}?active=true
     * GET /api/product-variants/product/{productId}?active=false
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getVariantsByProductId(
            @PathVariable Integer productId,
            @RequestParam(required = false) Boolean active) {
        
        List<ProductVariantResponse> variants = productVariantService.getVariantsByProductId(productId, active);
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantResponse>>builder()
                .code(1000)
                .message("Success")
                .result(variants)
                .build());
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createVariant(
            @Valid @ModelAttribute CreateProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductVariantResponse>builder()
                        .code(1000)
                        .message("Product variant created successfully")
                        .result(variant)
                        .build());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateVariant(
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateProductVariantRequest request) {
        ProductVariantResponse variant = productVariantService.updateVariant(id, request);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .code(1000)
                .message("Variant updated successfully")
                .result(variant)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Integer id) {
        productVariantService.deleteVariant(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(1000)
                .message("Variant deleted successfully")
                .build());
    }
}