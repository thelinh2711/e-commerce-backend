package com.example.shop_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ include field không null
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String sku;
    private BrandInfo brand;
    private PriceInfo price;
    private List<ImageInfo> images;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PriceInfo {
        private BigDecimal price;
        
        // ✅ Thêm costPrice - chỉ trả về cho OWNER
        @JsonProperty("cost_price")
        private BigDecimal costPrice;
        
        private String currency;
        
        @JsonProperty("discount_percent")
        private Integer discountPercent;
        
        @JsonProperty("discount_price")
        private Long discountPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageInfo {
        @JsonProperty("image_url")
        private String imageUrl;
        
        @JsonProperty("alt_text")
        private String altText;
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