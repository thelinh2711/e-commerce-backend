package com.example.shop_backend.dto.request;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductVariantRequest {
    
    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Color ID is required")
    private Integer colorId;
    
    @NotNull(message = "Size ID is required")
    private Integer sizeId;
    
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;
    
    // ✅ THAY ĐỔI: Nhận file ảnh thay vì URL
    @NotNull(message = "Images are required")
    private List<MultipartFile> images;
}