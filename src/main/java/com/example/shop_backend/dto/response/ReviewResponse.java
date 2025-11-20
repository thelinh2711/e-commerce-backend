package com.example.shop_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer productVariantId;
    private String productName;
    private String color;
    private String size;

    private Integer rating;   // 1-5 sao
    private String title;     // tự sinh theo rating
    private String comment;

    private LocalDateTime createdAt;
}
