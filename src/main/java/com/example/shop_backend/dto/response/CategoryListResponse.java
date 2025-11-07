package com.example.shop_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryListResponse {
    private boolean success;
    private List<CategoryResponse> data;
}
