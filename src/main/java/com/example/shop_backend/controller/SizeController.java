package com.example.shop_backend.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.SizeResponse;
import com.example.shop_backend.service.SizeService;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SizeResponse>>> getAll() {
        List<SizeResponse> sizes = sizeService.getAll();

        ApiResponse<List<SizeResponse>> response = ApiResponse.<List<SizeResponse>>builder()
                .code(1000)
                .message("Lấy danh sách kích thước thành công")
                .result(sizes)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SizeResponse>> getById(@PathVariable Integer id) {
        SizeResponse size = sizeService.getById(id);

        ApiResponse<SizeResponse> response = ApiResponse.<SizeResponse>builder()
                .code(1000)
                .message("Lấy kích thước thành công")
                .result(size)
                .build();

        return ResponseEntity.ok(response);
    }
}
