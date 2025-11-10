package com.example.shop_backend.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.LabelResponse;
import com.example.shop_backend.service.LabelService;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LabelResponse>>> getAll() {
        List<LabelResponse> labels = labelService.getAll();

        ApiResponse<List<LabelResponse>> response = ApiResponse.<List<LabelResponse>>builder()
                .code(1000)
                .message("Lấy danh sách nhãn thành công")
                .result(labels)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelResponse>> getById(@PathVariable Integer id) {
        LabelResponse label = labelService.getById(id);

        ApiResponse<LabelResponse> response = ApiResponse.<LabelResponse>builder()
                .code(1000)
                .message("Lấy nhãn thành công")
                .result(label)
                .build();

        return ResponseEntity.ok(response);
    }
}
