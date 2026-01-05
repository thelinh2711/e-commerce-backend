package com.example.shop_backend.dto.request;

import java.math.BigDecimal;

import com.example.shop_backend.model.enums.VoucherDiscountType;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVoucherRequest {
    
    private String code;
    
    private VoucherDiscountType discountType;
    
    @Min(value = 0, message = "Giá trị giảm phải >= 0")
    private BigDecimal discountValue;
    
    private BigDecimal maxDiscountValue;
    
    private BigDecimal minOrderValue;
    
    @Min(value = 1, message = "Giới hạn sử dụng phải >= 1")
    private Integer usageLimit;
    
    private String startDate; // Format: dd/MM/yyyy
    
    private String endDate; // Format: dd/MM/yyyy
    
    private String description;
}
