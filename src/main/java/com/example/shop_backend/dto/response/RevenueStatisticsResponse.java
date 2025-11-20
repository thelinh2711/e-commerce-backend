package com.example.shop_backend.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO cho thống kê doanh thu và lợi nhuận
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticsResponse {
    
    // Thống kê tổng quan (đầy đủ - cho OWNER)
    private OverviewStats overview;
    
    // Chi tiết theo đơn hàng (đầy đủ - cho OWNER)
    private List<OrderRevenueDetail> orderDetails;
    
    // Thống kê theo thời gian (đầy đủ - cho OWNER)
    private TimeRangeStats timeRange;
    
    /**
     * OverviewStats - Thống kê tổng quan đầy đủ
     * CHỈ OWNER mới thấy cost và profit
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OverviewStats {
        private BigDecimal totalRevenue;        // Tổng doanh thu (từ payments PAID)
        private BigDecimal totalCost;           // Tổng chi phí (cost_price * quantity)
        private BigDecimal totalProfit;         // Lợi nhuận = totalRevenue - totalCost
        private BigDecimal profitMargin;        // Tỷ suất lợi nhuận = (profit / revenue) * 100
        private Integer totalOrders;            // Tổng số đơn hàng đã thanh toán
        private Integer totalProducts;          // Tổng số sản phẩm đã bán
    }
    
    /**
     * RevenueOnlyStats - Thống kê chỉ có doanh thu
     * ADMIN và OWNER đều thấy được
     * KHÔNG có thông tin cost và profit
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueOnlyStats {
        private BigDecimal totalRevenue;        // Tổng doanh thu
        private Integer totalOrders;            // Tổng số đơn hàng
        private Integer totalProducts;          // Tổng số sản phẩm đã bán
        private LocalDateTime fromDate;         // Từ ngày
        private LocalDateTime toDate;           // Đến ngày
    }
    
    /**
     * OrderRevenueDetail - Chi tiết doanh thu theo đơn hàng
     * CHỈ OWNER mới thấy
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRevenueDetail {
        private Integer orderId;
        private String orderNumber;             // Format: DH000001
        private LocalDateTime orderDate;
        private BigDecimal orderRevenue;        // Doanh thu của đơn
        private BigDecimal orderCost;           // Chi phí của đơn
        private BigDecimal orderProfit;         // Lợi nhuận của đơn
        private String customerName;
        private String customerEmail;
        private List<ProductRevenueDetail> products;
    }
    
    /**
     * ProductRevenueDetail - Chi tiết doanh thu theo sản phẩm
     * CHỈ OWNER mới thấy
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductRevenueDetail {
        private Integer productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;           // Giá bán tại thời điểm đặt hàng
        private BigDecimal costPrice;           // Giá vốn
        private BigDecimal revenue;             // Doanh thu = unitPrice * quantity
        private BigDecimal cost;                // Chi phí = costPrice * quantity
        private BigDecimal profit;              // Lợi nhuận = revenue - cost
    }
    
    /**
     * TimeRangeStats - Thống kê theo khoảng thời gian
     * CHỈ OWNER mới thấy
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeRangeStats {
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private BigDecimal periodRevenue;       // Doanh thu trong kỳ
        private BigDecimal periodCost;          // Chi phí trong kỳ
        private BigDecimal periodProfit;        // Lợi nhuận trong kỳ
        private Integer periodOrders;           // Số đơn hàng trong kỳ
    }
}