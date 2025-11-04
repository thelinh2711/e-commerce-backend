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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Các API public
                        .requestMatchers("/api/auth/**").permitAll()
                        // Cho phép truy cập GET public cho products, nhưng POST/PUT/DELETE cần ADMIN
                        .requestMatchers("GET", "/api/products/**").permitAll()
                        // .requestMatchers("POST", "/api/products/**").permitAll()
                        // .requestMatchers("PUT", "/api/products/**").permitAll()
                        // .requestMatchers("DELETE", "/api/products/**").permitAll()

                        // Cho phép tất cả các yêu cầu GET đến thư mục public (chứa ảnh)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/public/**").permitAll()

                        // Các thao tác cần ADMIN
                        .requestMatchers("POST", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("PUT", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/api/products/**").hasRole("ADMIN")
                        // Các request khác cần có JWT
                        .anyRequest().authenticated()
               

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

                // ✅ Thêm JWT filter vào trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
