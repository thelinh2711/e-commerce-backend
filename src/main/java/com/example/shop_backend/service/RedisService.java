package com.example.shop_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveRefreshToken(String email, String refreshToken, long seconds) {
        redisTemplate.opsForValue().set("refresh_token:" + email, refreshToken, seconds, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("refresh_token:" + email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refresh_token:" + email);
    }

    public void deleteAllRefreshTokens(String email) {
        redisTemplate.delete("refresh_token:" + email);
    }

    // Lưu OTP
    public void saveOtp(String email, String otp, long minutes) {
        redisTemplate.opsForValue().set("otp:" + email, otp, minutes, TimeUnit.MINUTES);
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get("otp:" + email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete("otp:" + email);
    }

    // Lưu resetToken
    public void saveResetToken(String email, String token, long minutes) {
        redisTemplate.opsForValue().set("reset_token:" + email, token, minutes, TimeUnit.MINUTES);
    }

    public String getResetToken(String email) {
        return redisTemplate.opsForValue().get("reset_token:" + email);
    }

    public void deleteResetToken(String email) {
        redisTemplate.delete("reset_token:" + email);
    }
}
