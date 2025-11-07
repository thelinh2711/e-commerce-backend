package com.example.shop_backend.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
