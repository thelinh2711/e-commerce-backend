package com.example.shop_backend.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductVariantRequest {
    
    private Integer colorId;
    private Integer sizeId;
    private Integer stock;
    
    // ✅ THAY ĐỔI: Nhận file ảnh
    private List<MultipartFile> images;
}