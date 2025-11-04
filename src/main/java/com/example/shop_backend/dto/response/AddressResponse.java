package com.example.shop_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponse {
    private Integer id;
    private String street;
    private String district;
    private String city;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
