package com.example.shop_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.shop_backend.model.enums.StatusVoucher;
import com.example.shop_backend.model.enums.VoucherDiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    
    private Integer id;
    
    private String code;
    
    private VoucherDiscountType discountType;
    
    private BigDecimal discountValue;
    
    private BigDecimal maxDiscountValue;
    
    private BigDecimal minOrderValue;
    
    private Integer usageLimit;
    
    private Integer usageCount;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private StatusVoucher status;
    
    private String description;
    
    private LocalDateTime createdAt;
}
