package com.example.shop_backend.dto.request;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Integer orderId;           // Đơn hàng chứa sản phẩm
    private Integer productVariantId;  // Biến thể sản phẩm được review
    private Integer rating;            // 1–5
    private String comment;            // optional
}
