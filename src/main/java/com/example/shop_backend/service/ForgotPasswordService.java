package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.*;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.ForgotPasswordResponse;
import com.example.shop_backend.dto.response.ResetPasswordResponse;
import com.example.shop_backend.dto.response.VerifyOtpResponse;
import com.example.shop_backend.exception.AppException;
import com.example.shop_backend.exception.ErrorCode;
import com.example.shop_backend.model.User;
import com.example.shop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<ForgotPasswordResponse> sendOtp(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisService.saveOtp(user.getEmail(), otp, 5);

        emailService.sendEmail(user.getEmail(), "Mã OTP đặt lại mật khẩu",
                "Mã OTP của bạn là: " + otp + " (hiệu lực trong 5 phút)");

        ForgotPasswordResponse response = ForgotPasswordResponse.builder()
                .message("OTP đã được gửi đến email của bạn")
                .email(user.getEmail())
                .expiresIn(5)
                .build();

        return ApiResponse.success(response);
    }

    public ApiResponse<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request) {
        String savedOtp = redisService.getOtp(request.getEmail());
        if (savedOtp == null || !savedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        redisService.deleteOtp(request.getEmail());
        String resetToken = UUID.randomUUID().toString();
        redisService.saveResetToken(request.getEmail(), resetToken, 5);

        VerifyOtpResponse response = VerifyOtpResponse.builder()
                .message("Xác minh OTP thành công")
                .email(request.getEmail())
                .resetToken(resetToken)
                .expiresIn(5)
                .build();

        return ApiResponse.success(response);
    }

    public ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        String savedToken = redisService.getResetToken(request.getEmail());
        if (savedToken == null || !savedToken.equals(request.getResetToken())) {
            throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        redisService.deleteResetToken(request.getEmail());

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .message("Đặt lại mật khẩu thành công")
                .email(user.getEmail())
                .build();

        return ApiResponse.success(response);
    }

}
