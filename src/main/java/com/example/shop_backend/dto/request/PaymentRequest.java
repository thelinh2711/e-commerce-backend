package com.example.shop_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Integer orderId;      // ID order muốn thanh toán
    //private BigDecimal amount;    // Số tiền cần thanh toán
    private String bankCode;      // Optional: chọn ngân hàng (ví dụ "NCB", "VIB")
}
