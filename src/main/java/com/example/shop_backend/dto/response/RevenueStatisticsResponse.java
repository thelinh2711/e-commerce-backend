package com.example.shop_backend.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticsResponse {
    
    // Thống kê tổng quan
    private OverviewStats overview;
    
    // Chi tiết theo đơn hàng
    private List<OrderRevenueDetail> orderDetails;
    
    // Thống kê theo thời gian
    private TimeRangeStats timeRange;
    
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
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRevenueDetail {
        private Integer orderId;
        private String orderNumber;
        private LocalDateTime orderDate;
        private BigDecimal orderRevenue;        // Doanh thu của đơn
        private BigDecimal orderCost;           // Chi phí của đơn
        private BigDecimal orderProfit;         // Lợi nhuận của đơn
        private String customerName;
        private String customerEmail;
        private List<ProductRevenueDetail> products;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductRevenueDetail {
        private Integer productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;           // Giá bán
        private BigDecimal costPrice;           // Giá vốn
        private BigDecimal revenue;             // Doanh thu = unitPrice * quantity
        private BigDecimal cost;                // Chi phí = costPrice * quantity
        private BigDecimal profit;              // Lợi nhuận = revenue - cost
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeRangeStats {
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private BigDecimal periodRevenue;
        private BigDecimal periodCost;
        private BigDecimal periodProfit;
        private Integer periodOrders;
    }
}