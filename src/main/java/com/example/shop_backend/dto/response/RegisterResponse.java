package com.example.shop_backend.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private boolean success;
    private String message;
    private UserData data;

    @Data
    @Builder
    public static class UserData {
        private UserInfo user;
        private TokenInfo tokens;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Integer id;
        private String fullName;
        private String email;
        private String phone;
        private boolean email_verified;
        private String status;
        private LocalDateTime created_at;
        private String avatar;
    }

    @Data
    @Builder
    public static class TokenInfo {
        private String access_token;
        private String refresh_token;
        private int expires_in;
    }
}
