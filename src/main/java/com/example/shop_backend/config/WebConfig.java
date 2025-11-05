package com.example.shop_backend.config;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối của thư mục public
        String publicDir = new File("public").getAbsolutePath() + File.separator;
        
        // Cấu hình để serve static files từ thư mục public
        registry.addResourceHandler("/public/**")
                .addResourceLocations("file:///" + publicDir)
                .setCachePeriod(3600);
    }
}
