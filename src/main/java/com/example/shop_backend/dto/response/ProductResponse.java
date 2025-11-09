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
    private BrandInfo brand;
    private PriceInfo price;
    private List<String> images;
    private List<VariantInfo> variants;
    private List<CategoryInfo> categories;
    private List<LabelInfo> labels;
    
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
        
        private String currency;
        
        @JsonProperty("discount_percent")
        private Integer discountPercent;
        
        @JsonProperty("discount_price")
        private BigDecimal discountPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VariantInfo {
        private Integer id;
        private String size;
        private String image;
        private Integer stock;
        
        @JsonProperty("color_name")
        private String colorName;
        
        @JsonProperty("color_hex")
        private String colorHex;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Integer id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LabelInfo {
        private Integer id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandInfo {
        private Integer id;
        private String name;
    }
}
