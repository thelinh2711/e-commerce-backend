package com.example.shop_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String brand;
    private PriceInfo price;
    private List<String> images;
    private List<VariantInfo> variants;
    
    @JsonProperty("total_count")
    private Integer totalCount;
    
    @JsonProperty("sold")
    private Integer sold;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceInfo {
        private BigDecimal price;
        
        @JsonProperty("cost_price")
        private BigDecimal costPrice;
        
        private String currency;
        
        @JsonProperty("discount_percent")
        private Integer discountPercent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantInfo {
        private String size;
        private String image;
        private Integer stock;
        
        @JsonProperty("color_name")
        private String colorName;
        
        @JsonProperty("color_hex")
        private String colorHex;
    }
}
