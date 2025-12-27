package com.example.shop_backend.dto.response;

import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.model.enums.PaymentMethod;
import com.example.shop_backend.model.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponse {

    private Integer id;
    private String fullName;
    private String phone;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}

