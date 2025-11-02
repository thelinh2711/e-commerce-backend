package com.example.shop_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Tắt CSRF (do ta dùng REST API, không dùng form submission)
                .csrf(csrf -> csrf.disable())

                // ✅ Cấu hình phân quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép không cần xác thực cho các API public (auth, login, register)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Cho phép truy cập public cho products
                        .requestMatchers("/api/products/**").permitAll()
                        // Các request khác cần có JWT
                        .anyRequest().authenticated()
                )

                // ✅ Không dùng session (stateless) vì ta dùng JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )

                // ✅ Tạm thời tắt form login & logout mặc định
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    // ✅ Cấu hình mã hóa mật khẩu bằng BCrypt (dùng cho login/register thông thường)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
