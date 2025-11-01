package com.example.shop_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    // Thời gian sống của Access Token (tính bằng giây)
    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    // Thời gian sống của Refresh Token (tính bằng giây)
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Sinh khóa ký cho JWT
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sinh Access Token mặc định (dùng cho đăng ký, login thường)
     */
    public String generateAccessToken(String email) {
        return buildToken(email, accessExpiration);
    }

    /**
     * Sinh Access Token có remember_me (gấp 7 lần thời hạn bình thường)
     */
    public String generateAccessToken(String email, boolean rememberMe) {
        long exp = rememberMe ? accessExpiration * 7 : accessExpiration;
        return buildToken(email, exp);
    }

    /**
     * Sinh Refresh Token (dùng để lấy access token mới)
     */
    public String generateRefreshToken(String email) {
        return buildToken(email, refreshExpiration);
    }

    /**
     * Hàm chung để build JWT token
     */
    private String buildToken(String email, long expirationSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Xác thực token có hợp lệ không
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Lấy email từ token
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Lấy thời gian hết hạn còn lại (đơn vị: milliseconds)
     */
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
