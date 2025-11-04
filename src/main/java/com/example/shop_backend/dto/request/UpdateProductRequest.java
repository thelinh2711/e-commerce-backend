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
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    
    private String description;
    
    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải >= 0")
    private BigDecimal price;
    
    private BigDecimal discountPrice;
    
    @Min(value = 0, message = "Phần trăm giảm giá phải >= 0")
    private Integer discountPercent;
    
    @NotNull(message = "Brand ID không được để trống")
    private Integer brandId;
    
    @NotBlank(message = "SKU không được để trống")
    private String sku;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer stock;
    
    @NotBlank(message = "Slug không được để trống")
    private String slug;
    
    private String status; // ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED
    
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
        private Boolean isThumbnail;
        private Integer displayOrder;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantRequest {
        private Integer id; // Nếu có ID thì update, không có thì tạo mới
        private Integer colorId;
        private Integer sizeId;
        private String sku;
        private Integer stock;
        private BigDecimal price;
        private List<String> images; // URLs for variant images
    }
}
