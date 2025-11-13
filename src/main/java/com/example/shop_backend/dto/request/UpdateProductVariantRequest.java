package com.example.shop_backend.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductVariantRequest {
    
    private Integer colorId;
    
    private Integer sizeId;
    
    private Integer stock;
    
    private List<String> images;
}
