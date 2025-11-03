package com.example.shop_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

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
