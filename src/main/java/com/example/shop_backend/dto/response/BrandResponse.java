package com.example.shop_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandResponse {
    private Integer id;
    private String name;
    private String logo;
    private String description;
    private Boolean isActive;
}
