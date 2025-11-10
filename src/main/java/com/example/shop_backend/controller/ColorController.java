package com.example.shop_backend.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ColorResponse;
import com.example.shop_backend.service.ColorService;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAll() {
        List<ColorResponse> colors = colorService.getAll();

        ApiResponse<List<ColorResponse>> response = ApiResponse.<List<ColorResponse>>builder()
                .code(1000)
                .message("Lấy danh sách màu sắc thành công")
                .result(colors)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColorResponse>> getById(@PathVariable Integer id) {
        ColorResponse color = colorService.getById(id);

        ApiResponse<ColorResponse> response = ApiResponse.<ColorResponse>builder()
                .code(1000)
                .message("Lấy màu sắc thành công")
                .result(color)
                .build();

        return ResponseEntity.ok(response);
    }
}
