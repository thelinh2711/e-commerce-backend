package com.example.shop_backend.config;

import com.example.shop_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Tắt CSRF vì dùng JWT
                .csrf(csrf -> csrf.disable())

                // ✅ Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // API public
                        .requestMatchers("/api/auth/**").permitAll()

                        // Cho phép GET sản phẩm công khai
                        .requestMatchers("GET", "/api/products/**").permitAll()

                        // Các thao tác cần ADMIN
                        .requestMatchers("POST", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/products/**").hasRole("ADMIN")

                        // CUSTOMER được đổi mật khẩu, cập nhật profile
                        .requestMatchers("/api/users/change-password", "/api/users/update-profile").hasAuthority("CUSTOMER")

                        // ADMIN truy cập admin area
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // Các request còn lại cần đăng nhập
                        .anyRequest().authenticated()
                )

                // ✅ Stateless session vì dùng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Tắt form login + logout mặc định
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())

                // ✅ Thêm JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
