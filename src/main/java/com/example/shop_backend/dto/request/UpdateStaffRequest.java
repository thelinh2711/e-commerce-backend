package com.example.shop_backend.dto.request;

import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.model.enums.UserStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateStaffRequest {

    @Size(min = 3, max = 100, message = "Họ tên phải từ 3-100 ký tự")
    private String fullName;

    @Size(min = 9, max = 15, message = "Số điện thoại không hợp lệ")
    private String phone;

    // OWNER mới được gửi field này
    private Role role;

    // OWNER + ADMIN dùng
    private UserStatus status;
}
