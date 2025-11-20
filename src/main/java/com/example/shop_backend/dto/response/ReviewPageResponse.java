package com.example.shop_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewPageResponse {
    private Page<ReviewResponse> reviews;
    private Double averageRating; // trung bình
}
