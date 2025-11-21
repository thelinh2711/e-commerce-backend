package com.example.shop_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO cho thống kê doanh thu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticsRequest {
    
    /**
     * Ngày bắt đầu (bao gồm)
     * Format: yyyy-MM-dd HH:mm:ss
     * Nếu null, sẽ lấy từ đơn hàng đầu tiên
     */
    private LocalDateTime fromDate;
    
    /**
     * Ngày kết thúc (bao gồm)
     * Format: yyyy-MM-dd HH:mm:ss
     * Nếu null, sẽ lấy đến hiện tại
     */
    private LocalDateTime toDate;
    
    /**
     * Payment ID để lọc (lấy tối đa 30 payment từ ID này trở đi)
     * Nếu null, sẽ lấy 30 payment gần fromDate nhất
     */
    private Integer paymentId;
    
    /**
     * Số lượng bản ghi tối đa (mặc định 30)
     */
    @Builder.Default
    private Integer limit = 30;
}