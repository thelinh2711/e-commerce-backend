package com.example.shop_backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    
    private String description;
    
    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 0, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private BigDecimal price;
    
    private Integer discountPercent;
    
    @NotNull(message = "Brand ID không được để trống")
    private Integer brandId;
    
    // Danh sách category IDs - bắt buộc phải có ít nhất 1 category
    @NotNull(message = "Sản phẩm phải thuộc ít nhất 1 danh mục")
    private List<Integer> categoryIds;
    
    // Danh sách label IDs
    private List<Integer> labelIds;
    
    // Danh sách ảnh sản phẩm (có thể null hoặc rỗng)
    @NotNull(message = "Ảnh không được để trống")
    private List<ProductImageRequest> images;
    
    // Danh sách biến thể
    private List<ProductVariantRequest> variants;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductImageRequest {
        @NotBlank(message = "Image URL không được để trống")
        private String imageUrl;
        
        private String altText;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantRequest {
        private Integer colorId;
        
        private Integer sizeId;

        @NotNull(message = "Stock biến thể không được để trống")
        @Min(value = 0, message = "Stock biến thể phải lớn hơn hoặc bằng 0")
        private Integer stock;
        
        // Ảnh riêng của biến thể
        private List<String> images;
    }
}
