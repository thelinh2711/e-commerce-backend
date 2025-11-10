package com.example.shop_backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;
}
