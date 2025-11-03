package com.example.shop_backend.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
