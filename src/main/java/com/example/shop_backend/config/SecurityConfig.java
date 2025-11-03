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
@RequiredArgsConstructor
@EnableMethodSecurity  // ✅ Cho phép dùng @PreAuthorize nếu cần sau này
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Các API public
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ Chỉ CUSTOMER mới được đổi mật khẩu và cập nhật thông tin cá nhân
                        .requestMatchers("/api/users/change-password", "/api/users/update-profile")
                        .hasAuthority("CUSTOMER")

                        // ✅ Chỉ ADMIN mới được truy cập khu vực quản trị
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // ✅ Các request còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                )
                // ✅ Stateless session vì dùng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
