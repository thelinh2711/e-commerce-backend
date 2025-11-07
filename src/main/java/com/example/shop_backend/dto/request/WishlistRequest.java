package com.example.shop_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistRequest {
    @NotNull(message = "Vui lòng nhập ID sản phẩm")
    private Integer productId;
}
