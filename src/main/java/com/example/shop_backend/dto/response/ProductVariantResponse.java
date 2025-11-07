package com.example.shop_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    
    private Integer id;
    private Integer productId;
    private String productName;
    private String sku;
    private Integer stock;
    private BigDecimal price;
    private ColorInfo color;
    private SizeInfo size;
    private List<String> images;
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ColorInfo {
        private Integer id;
        private String name;
        private String hexCode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SizeInfo {
        private Integer id;
        private String name;
    }
}
