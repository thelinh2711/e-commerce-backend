package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.ForgotPasswordRequest;
import com.example.shop_backend.dto.request.VerifyOtpRequest;
import com.example.shop_backend.dto.request.ResetPasswordRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ForgotPasswordResponse;
import com.example.shop_backend.dto.response.VerifyOtpResponse;
import com.example.shop_backend.dto.response.ResetPasswordResponse;
import com.example.shop_backend.service.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        ApiResponse<ForgotPasswordResponse> resp = forgotPasswordService.sendOtp(request);
        return ResponseEntity.status(resp.getCode() == 1000 ? 200 : 400).body(resp);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        ApiResponse<VerifyOtpResponse> resp = forgotPasswordService.verifyOtp(request);
        return ResponseEntity.status(resp.getCode() == 1000 ? 200 : 400).body(resp);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        ApiResponse<ResetPasswordResponse> resp = forgotPasswordService.resetPassword(request);
        return ResponseEntity.status(resp.getCode() == 1000 ? 200 : 400).body(resp);
    }
}
