package com.example.shop_backend.dto.request;

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

    @NotNull(message = "Color ID is required")
    private Integer colorId;
    
    @NotNull(message = "Size ID is required")
    private Integer sizeId;
    
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;
    
    @NotNull(message = "Image URLs are required")
    private List<String> images;
}
