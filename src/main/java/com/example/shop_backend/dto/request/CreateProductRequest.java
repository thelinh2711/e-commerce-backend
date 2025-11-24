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
import org.springframework.web.multipart.MultipartFile;

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
    
    @Min(value = 0, message = "Giá vốn phải lớn hơn hoặc bằng 0")
    private BigDecimal costPrice;
    
    private Integer discountPercent;
    
    @NotNull(message = "Brand ID không được để trống")
    private Integer brandId;
    
    @NotNull(message = "Sản phẩm phải thuộc ít nhất 1 danh mục")
    private List<Integer> categoryIds;
    
    private List<Integer> labelIds;
    
    // ✅ THAY ĐỔI: Nhận danh sách file ảnh
    @NotNull(message = "Ảnh không được để trống")
    private List<MultipartFile> images;
    
    // ✅ THAY ĐỔI: Alt text cho từng ảnh (optional)
    private List<String> imageAltTexts;
    
    // Danh sách biến thể
    private List<ProductVariantRequest> variants;
    
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
        
        // ✅ THAY ĐỔI: Nhận danh sách file ảnh cho variant
        private List<MultipartFile> images;
    }
}