package com.example.shop_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

                        // API Public
                        .requestMatchers("/api/auth/**").permitAll()

                        // Cho phép AI GET categories công khai
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        // Cho phép truy cập static files (images, css, js)
                        .requestMatchers("/public/**").permitAll()

                        // Cho phép GET sản phẩm công khai
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // Cho phép ai cũng xem danh sách sản phẩm được thích nhiều nhất
                        .requestMatchers(HttpMethod.GET, "/api/wishlist/top-liked").permitAll()

                        // ADMIN quản lý Category & Product
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // CUSTOMER thao tác user info
                        .requestMatchers("/api/users/change-password", "/api/users/update-profile")
                        .hasRole("CUSTOMER")

                        // ADMIN area
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Tất cả còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
