package com.example.shop_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpResponse {
    private String message;     // Thông báo
    private String email;       // Email người dùng
    private String resetToken;  // Token đặt lại mật khẩu
    private int expiresIn;      // Thời gian sống của token (phút)
}
