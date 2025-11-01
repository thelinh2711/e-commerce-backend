package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.LoginRequest;
import com.example.shop_backend.dto.request.RegisterRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.LoginResponse;
import com.example.shop_backend.dto.response.RegisterResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.mapper.UserMapper;
import com.example.shop_backend.model.User;
import com.example.shop_backend.repository.UserRepository;
import com.example.shop_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    public RegisterResponse register(RegisterRequest request) {
        // 🔒 Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 🔐 Kiểm tra xác nhận mật khẩu
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
        }

        // 🤖 Kiểm tra captcha
        if (request.getCaptcha_token() == null || request.getCaptcha_token().isEmpty()) {
            throw new AppException(ErrorCode.CAPTCHA_REQUIRED);
        }

        // 🧩 MapStruct -> Entity
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("https://e7.pngegg.com/pngimages/84/165/png-clipart-united-states-avatar-organization-information-user-avatar-service-computer-wallpaper.png");
        }

        // Lưu vào DB
        userRepository.save(user);

        // Tạo JWT token
        String accessToken = jwtUtils.generateToken(user.getEmail());
        String refreshToken = jwtUtils.generateToken(user.getEmail());

        // Trả response
        return RegisterResponse.builder()
                .success(true)
                .message("Tài khoản đã được tạo thành công")
                .data(RegisterResponse.UserData.builder()
                        .user(RegisterResponse.UserInfo.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .email_verified(false)
                                .status(user.getStatus().name().toLowerCase())
                                .created_at(user.getCreatedAt())
                                .avatar(user.getAvatar())
                                .build())
                        .tokens(RegisterResponse.TokenInfo.builder()
                                .access_token(accessToken)
                                .refresh_token(refreshToken)
                                .expires_in(3600)
                                .build())
                        .build())
                .build();
    }

    public ApiResponse<LoginResponse.LoginData> login(LoginRequest request) {

        // Kiểm tra captcha
        if (request.getCaptcha_response() == null || request.getCaptcha_response().isEmpty()) {
            throw new AppException(ErrorCode.CAPTCHA_REQUIRED);
        }

        // Kiểm tra email tồn tại
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Sinh token JWT
        String accessToken = jwtUtils.generateToken(user.getEmail());
        String refreshToken = jwtUtils.generateToken(user.getEmail());

        // Tạo dữ liệu phản hồi
        LoginResponse.LoginData data = LoginResponse.LoginData.builder()
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .email_verified(false)
                        .status(user.getStatus().name().toLowerCase())
                        .created_at(user.getCreatedAt())
                        .avatar(user.getAvatar())
                        .build())
                .tokens(LoginResponse.TokenInfo.builder()
                        .access_token(accessToken)
                        .refresh_token(refreshToken)
                        .expires_in(request.isRemember_me() ? 604800 : 3600) // 7 ngày hoặc 1 giờ
                        .build())
                .build();

        // Trả về response
        return new ApiResponse<>(1000, "Đăng nhập thành công", data);
    }

}
