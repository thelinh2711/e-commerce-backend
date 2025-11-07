package com.example.shop_backend.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.request.CategoryRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.CategoryListResponse;
import com.example.shop_backend.dto.response.CategoryResponse;
import com.example.shop_backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Integer id) {
        CategoryResponse category = categoryService.getById(id);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .code(1000)
                .message("Lấy danh mục thành công")
                .result(category)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.create(request);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .code(1000)
                .message("Tạo danh mục thành công")
                .result(category)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category = categoryService.update(id, request);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .code(1000)
                .message("Cập nhật danh mục thành công")
                .result(category)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        categoryService.delete(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa danh mục thành công")
                .build();

        return ResponseEntity.ok(response);
    }
}
