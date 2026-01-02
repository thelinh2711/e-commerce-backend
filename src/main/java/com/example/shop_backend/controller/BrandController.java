package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.BrandRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.BrandResponse;
import com.example.shop_backend.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // Lấy tất cả thương hiệu
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thương hiệu thành công", brands));
    }

    // Lấy thương hiệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable Integer id) {
        BrandResponse brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thương hiệu thành công", brand));
    }

    // Tạo thương hiệu (ADMIN)
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandRequest request) {
        BrandResponse created = brandService.createBrand(request);
        return ResponseEntity.ok(ApiResponse.success("Thêm thương hiệu thành công", created));
    }

    // Cập nhật thương hiệu (ADMIN)
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Integer id,
            @RequestBody BrandRequest request) {
        BrandResponse updated = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thương hiệu thành công", updated));
    }

    // Xóa thương hiệu (ADMIN)
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thương hiệu thành công", null));
    }
}
