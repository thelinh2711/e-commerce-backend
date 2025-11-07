package com.example.shop_backend.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductVariantRequest {
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    private Integer colorId;
    
    private Integer sizeId;
    
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;
    
    private BigDecimal price;
    
    // Optional list of image URLs for this variant
    private List<String> images;
}
