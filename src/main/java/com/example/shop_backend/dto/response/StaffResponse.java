package com.example.shop_backend.dto.response;

import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.model.enums.UserStatus;
import lombok.Data;

@Data
public class StaffResponse {

    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
}
