package com.example.shop_backend.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LabelResponse {
    private Integer id;
    private String name;
    private String color;
}
