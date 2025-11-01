package com.example.shop_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "Vui lòng nhập email")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    private boolean remember_me;

    @NotBlank(message = "Vui lòng xác minh captcha")
    private String captcha_response;
}
