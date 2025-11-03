package com.example.shop_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message; // Thông báo
    private String email;   // Email đã gửi OTP
    private int expiresIn;  // Thời gian hết hạn (phút)
}
