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
}