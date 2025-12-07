package com.example.shop_backend.dto.request;

import com.example.shop_backend.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, max = 100, message = "Họ tên phải từ 3-100 ký tự")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 9, max = 15, message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "Mật khẩu xác nhận không được để trống")
    private String confirmPassword;

    @NotNull(message = "Vai trò không được để trống")
    private Role role; // ADMIN / EMPLOYEE
}
