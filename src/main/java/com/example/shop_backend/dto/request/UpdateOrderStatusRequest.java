package com.example.shop_backend.dto.request;

import com.example.shop_backend.model.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}
