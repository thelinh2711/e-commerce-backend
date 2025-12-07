package com.example.shop_backend.controller;

import java.util.List;

import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.model.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shop_backend.dto.request.CreateUserRequest;
import com.example.shop_backend.dto.request.ResetStaffPasswordRequest;
import com.example.shop_backend.dto.request.UpdateStaffRequest;
import com.example.shop_backend.dto.response.StaffResponse;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.service.OwnerUserService;

@RestController
@RequestMapping("/api/owner/users")
@RequiredArgsConstructor
public class OwnerUserController {

    private final OwnerUserService ownerUserService;

    // OWNER / ADMIN tạo user
    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> create(
            @RequestBody @Valid CreateUserRequest request) {

        StaffResponse response = ownerUserService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.success("Tạo tài khoản thành công", response)
        );
    }

    // OWNER / ADMIN update + block
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> update(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateStaffRequest request) {

        StaffResponse response = ownerUserService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật tài khoản thành công", response)
        );
    }

    // OWNER / ADMIN reset password
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Integer id,
            @RequestBody @Valid ResetStaffPasswordRequest request) {

        ownerUserService.resetPassword(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Reset mật khẩu thành công", null)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StaffResponse>>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StaffResponse> users = ownerUserService.getUsers(keyword, role, status, page, size);

        return ResponseEntity.ok(ApiResponse.success("Danh sách người dùng", users));
    }
}
