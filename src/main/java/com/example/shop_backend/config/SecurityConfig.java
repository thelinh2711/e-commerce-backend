package com.example.shop_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.shop_backend.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Tắt CSRF (do ta dùng REST API, không dùng form submission)
                .csrf(csrf -> csrf.disable())

                // ✅ Cấu hình phân quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép không cần xác thực cho các API public (auth, login, register)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Cho phép truy cập GET public cho products, nhưng POST/PUT/DELETE cần ADMIN
                        .requestMatchers("GET", "/api/products/**").permitAll()
                        // .requestMatchers("POST", "/api/products/**").permitAll()
                        // .requestMatchers("PUT", "/api/products/**").permitAll()
                        // .requestMatchers("DELETE", "/api/products/**").permitAll()
                        .requestMatchers("POST", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/products/**").hasRole("ADMIN")
                        // Các request khác cần có JWT
                        .anyRequest().authenticated()
                )

                // ✅ Không dùng session (stateless) vì ta dùng JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )

                // ✅ Tạm thời tắt form login & logout mặc định
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())

                // ✅ Thêm JWT filter vào trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Cấu hình mã hóa mật khẩu bằng BCrypt (dùng cho login/register thông thường)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
