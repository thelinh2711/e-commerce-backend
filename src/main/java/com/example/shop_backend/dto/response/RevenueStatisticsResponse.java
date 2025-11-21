package com.example.shop_backend.dto.response;

import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.model.enums.PaymentMethod;
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
    
    /**
     * OverviewStats - Thống kê tổng quan đầy đủ
     * CHỈ OWNER mới thấy cost và profit
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OverviewStats {
        private BigDecimal totalRevenue;
        private BigDecimal totalCost;
        private BigDecimal totalProfit;
        private BigDecimal profitMargin;
        private Integer totalOrders;
        private Integer totalProducts;
    }
    
    /**
     * RevenueOnlyStats - Thống kê chỉ có doanh thu
     * ADMIN và OWNER đều thấy được
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueOnlyStats {
        private BigDecimal totalRevenue;
        private Integer totalOrders;
        private Integer totalProducts;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private List<OrderRevenueDetail> orderDetails;
    }
    
    /**
     * OrderRevenueDetail - Chi tiết doanh thu theo đơn hàng
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderRevenueDetail {
        private Integer orderId;
        private LocalDateTime orderDate;
        private BigDecimal orderRevenue;
        private BigDecimal orderCost;
        private BigDecimal orderProfit;
        private String customerName;
        private String customerEmail;
        private OrderStatus orderStatus;
        private PaymentMethod paymentMethod;
        private List<ProductRevenueDetail> products;
    }
    
    /**
     * ProductRevenueDetail - Chi tiết doanh thu theo sản phẩm
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
        private BigDecimal unitPrice;
        private BigDecimal costPrice;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
        private ProductVariantInfo productVariant;
    }
    
    /**
     * ProductVariantInfo - Thông tin biến thể sản phẩm
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductVariantInfo {
        private Integer variantId;
        private Integer quantity;
        private String color;
        private String size;
    }
}