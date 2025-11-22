package com.example.shop_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO cho thống kê doanh thu và lợi nhuận
 * Sử dụng cho cả 8 API endpoints (4 revenue + 4 profit)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponse {
    
    /**
     * Tổng doanh thu (tổng giá bán)
     */
    private BigDecimal totalRevenue;
    
    /**
     * Tổng giá gốc (chỉ có ở profit API, revenue API sẽ null)
     */
    private BigDecimal totalCost;
    
    /**
     * Tổng lợi nhuận = totalRevenue - totalCost (chỉ có ở profit API)
     */
    private BigDecimal totalProfit;
    
    /**
     * Tổng số đơn hàng
     */
    private Integer totalOrders;
    
    /**
     * Tổng số sản phẩm bán được
     */
    private Integer totalProducts;
    
    /**
     * Từ ngày
     */
    private LocalDate fromDate;
    
    /**
     * Đến ngày
     */
    private LocalDate toDate;
    
    /**
     * Chi tiết theo ngày/tháng
     */
    private List<DailyStatistic> details;
    
    /**
     * Chi tiết thống kê theo ngày hoặc tháng
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStatistic {
        
        /**
         * Ngày hoặc tháng
         * - Với API date-range, week, month: "yyyy-MM-dd" (ví dụ: "2025-01-22")
         * - Với API year: "yyyy-MM" (ví dụ: "2025-01")
         */
        private String date;
        
        /**
         * Doanh thu (tổng giá bán)
         */
        private BigDecimal revenue;
        
        /**
         * Giá gốc (chỉ có ở profit API)
         */
        private BigDecimal cost;
        
        /**
         * Lợi nhuận = revenue - cost (chỉ có ở profit API)
         */
        private BigDecimal profit;
        
        /**
         * Số đơn hàng trong ngày/tháng này
         */
        private Integer orderCount;
        
        /**
         * Số sản phẩm bán được trong ngày/tháng này
         */
        private Integer productCount;
    }
}