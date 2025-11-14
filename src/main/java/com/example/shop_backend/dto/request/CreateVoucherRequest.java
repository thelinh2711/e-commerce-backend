package com.example.shop_backend.dto.request;

import java.math.BigDecimal;

import com.example.shop_backend.model.enums.VoucherDiscountType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoucherRequest {
    
    @NotBlank(message = "Mã voucher không được để trống")
    private String code;
    
    @NotNull(message = "Loại giảm giá không được để trống")
    private VoucherDiscountType discountType;
    
    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0, message = "Giá trị giảm phải >= 0")
    private BigDecimal discountValue;
    
    private BigDecimal maxDiscountValue;
    
    private BigDecimal minOrderValue;
    
    @NotNull(message = "Giới hạn sử dụng không được để trống")
    @Min(value = 1, message = "Giới hạn sử dụng phải >= 1")
    private Integer usageLimit;
    
    @NotBlank(message = "Ngày bắt đầu không được để trống")
    private String startDate; // Format: dd/MM/yyyy
    
    @NotBlank(message = "Ngày kết thúc không được để trống")
    private String endDate; // Format: dd/MM/yyyy
    
    private String description;
}
