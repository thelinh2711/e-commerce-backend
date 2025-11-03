package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.ChangePasswordRequest;
import com.example.shop_backend.dto.request.UpdateUserRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.User;
import com.example.shop_backend.model.enums.Role;
import com.example.shop_backend.security.JwtUtils;
import com.example.shop_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    /**
     * API đổi mật khẩu — chỉ dành cho user đã đăng nhập
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChangePasswordRequest request
    ) {
        // Lấy token JWT từ header
        String token = authorizationHeader.replace("Bearer ", "").trim();

        // Giải mã email từ token
        String email = jwtUtils.getEmailFromToken(token);

        // Thực hiện đổi mật khẩu
        userService.changePassword(email, request);

        // Trả phản hồi thành công
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (user.getRole() != Role.CUSTOMER) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        userService.updateUserInfo(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", null));
    }
}
