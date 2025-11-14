package com.example.shop_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_backend.dto.request.CreateVoucherRequest;
import com.example.shop_backend.dto.request.UpdateVoucherRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.VoucherResponse;
import com.example.shop_backend.model.enums.StatusVoucher;
import com.example.shop_backend.service.VoucherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    
    private final VoucherService voucherService;

    /**
     * Tạo voucher mới
     * POST /api/vouchers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(
            @Valid @RequestBody CreateVoucherRequest request) {
        VoucherResponse response = voucherService.createVoucher(request);
        return ResponseEntity.ok(ApiResponse.<VoucherResponse>builder()
                .result(response)
                .build());
    }

    /**
     * Cập nhật voucher
     * PUT /api/vouchers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucher(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateVoucherRequest request) {
        VoucherResponse response = voucherService.updateVoucher(id, request);
        return ResponseEntity.ok(ApiResponse.<VoucherResponse>builder()
                .result(response)
                .build());
    }

    /**
     * Xóa voucher
     * DELETE /api/vouchers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Xóa voucher thành công")
                .build());
    }

    /**
     * Lấy voucher theo ID
     * GET /api/vouchers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherById(@PathVariable Integer id) {
        VoucherResponse response = voucherService.getVoucherById(id);
        return ResponseEntity.ok(ApiResponse.<VoucherResponse>builder()
                .result(response)
                .build());
    }

    /**
     * Lấy voucher theo code
     * GET /api/vouchers/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getVoucherByCode(@PathVariable String code) {
        VoucherResponse response = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(ApiResponse.<VoucherResponse>builder()
                .result(response)
                .build());
    }

    /**
     * Lấy tất cả vouchers
     * GET /api/vouchers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getAllVouchers(
            @RequestParam(required = false) StatusVoucher status) {
        List<VoucherResponse> responses;
        
        if (status != null) {
            responses = voucherService.getVouchersByStatus(status);
        } else {
            responses = voucherService.getAllVouchers();
        }
        
        return ResponseEntity.ok(ApiResponse.<List<VoucherResponse>>builder()
                .result(responses)
                .build());
    }

    /**
     * Lấy vouchers đang active
     * GET /api/vouchers/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getActiveVouchers() {
        List<VoucherResponse> responses = voucherService.getActiveVouchers();
        return ResponseEntity.ok(ApiResponse.<List<VoucherResponse>>builder()
                .result(responses)
                .build());
    }

    /**
     * Validate voucher
     * GET /api/vouchers/validate/{code}
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<ApiResponse<VoucherResponse>> validateVoucher(@PathVariable String code) {
        VoucherResponse response = voucherService.validateVoucher(code);
        return ResponseEntity.ok(ApiResponse.<VoucherResponse>builder()
                .result(response)
                .message("Voucher hợp lệ")
                .build());
    }

    /**
     * Cập nhật trạng thái vouchers đã hết hạn
     * PUT /api/vouchers/update-expired
     */
    @PutMapping("/update-expired")
    public ResponseEntity<ApiResponse<Void>> updateExpiredVouchers() {
        voucherService.updateExpiredVouchers();
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Cập nhật vouchers hết hạn thành công")
                .build());
    }
}
