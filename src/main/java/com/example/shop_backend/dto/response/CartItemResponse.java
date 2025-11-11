package com.example.shop_backend.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Integer id;
    private Integer productVariantId;
    private String productName;
    private String colorName;
    private String sizeName;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer quantity;
    private BigDecimal itemTotalPrice;
}
