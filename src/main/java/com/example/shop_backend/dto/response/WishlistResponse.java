package com.example.shop_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Integer productId;
    private String sku;
    private String name;
    private String brand;
    private PriceInfo price;
    private List<String> images;
    private Integer sold;
    private Integer likeCount; // số lượt yêu thích
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceInfo {
        private BigDecimal discountPrice;
        private BigDecimal price;
        @JsonProperty("discount_percent")
        private Integer discountPercent;
        private String currency;
    }
}
