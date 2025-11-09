package com.example.shop_backend.dto.request;

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
public class UpdateProductVariantStockRequest {
    
    @NotNull(message = "Import new stock is required")
    @PositiveOrZero(message = "Import new stock must be zero or positive")
    private Integer importNewStock;
}
