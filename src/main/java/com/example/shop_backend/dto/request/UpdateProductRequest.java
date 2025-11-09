package com.example.shop_backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    
    private String name;
    
    private String description;

    @Min(value = 0, message = "Giá sản phẩm phải >= 0")
    private BigDecimal price;
    
    private BigDecimal discountPrice;
    
    @Min(value = 0, message = "Phần trăm giảm giá phải >= 0")
    private Integer discountPercent;

    private Integer brandId;

    private Integer stock;
    
    private List<Integer> categoryIds;
    
    private List<Integer> labelIds;
    
    private List<ProductImageRequest> images;
    
    private List<ProductVariantRequest> variants;
    
    // Inner classes để cập nhật images và variants
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageRequest {
        private Integer id; // Nếu có ID thì update, không có thì tạo mới
        private String imageUrl;
        private String altText;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantRequest {
        private Integer id; // Nếu có ID thì update, không có thì tạo mới
        private Integer colorId;
        private Integer sizeId;
        private Integer stock;
        private BigDecimal price;
        private List<String> images; // URLs for variant images
    }
}
