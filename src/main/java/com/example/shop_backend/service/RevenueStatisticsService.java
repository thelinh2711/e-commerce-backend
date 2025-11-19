package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.RevenueStatisticsRequest;
import com.example.shop_backend.dto.response.RevenueStatisticsResponse;
import com.example.shop_backend.dto.response.RevenueStatisticsResponse.*;
import com.example.shop_backend.model.*;
import com.example.shop_backend.model.enums.PaymentStatus;
import com.example.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueStatisticsService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Lấy thống kê doanh thu và lợi nhuận theo khoảng thời gian
     * Chỉ OWNER mới được gọi method này
     */
    @Transactional(readOnly = true)
    public RevenueStatisticsResponse getRevenueStatistics(RevenueStatisticsRequest request) {
        
        LocalDateTime fromDate = request.getFromDate();
        LocalDateTime toDate = request.getToDate();
        
        // Lấy tất cả payment đã thanh toán trong khoảng thời gian
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        // Tính toán thống kê tổng quan
        OverviewStats overview = calculateOverviewStats(payments);
        
        // Lấy chi tiết từng đơn hàng
        List<OrderRevenueDetail> orderDetails = calculateOrderDetails(payments);
        
        // Thống kê theo khoảng thời gian
        TimeRangeStats timeRange = calculateTimeRangeStats(payments, fromDate, toDate);
        
        return RevenueStatisticsResponse.builder()
                .overview(overview)
                .orderDetails(orderDetails)
                .timeRange(timeRange)
                .build();
    }

    /**
     * Lấy payments đã thanh toán theo filter
     */
    private List<Payment> getFilteredPayments(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> allPaidPayments = paymentRepository.findByStatus(PaymentStatus.PAID);
        
        return allPaidPayments.stream()
                .filter(p -> {
                    if (fromDate != null && p.getCreatedAt().isBefore(fromDate)) {
                        return false;
                    }
                    if (toDate != null && p.getCreatedAt().isAfter(toDate)) {
                        return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Tính thống kê tổng quan
     */
    private OverviewStats calculateOverviewStats(List<Payment> payments) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalProducts = 0;
        
        for (Payment payment : payments) {
            Order order = payment.getOrder();
            
            // Cộng doanh thu
            totalRevenue = totalRevenue.add(payment.getAmount());
            
            // Tính chi phí từ order items
            for (OrderItem item : order.getItems()) {
                Product product = item.getProductVariant().getProduct();
                
                // Lấy cost_price, nếu null thì dùng unit_price
                BigDecimal costPrice = product.getCostPrice() != null 
                    ? product.getCostPrice() 
                    : item.getUnitPrice();
                
                BigDecimal itemCost = costPrice.multiply(new BigDecimal(item.getQuantity()));
                totalCost = totalCost.add(itemCost);
                totalProducts += item.getQuantity();
            }
        }
        
        BigDecimal totalProfit = totalRevenue.subtract(totalCost);
        
        // Tính tỷ suất lợi nhuận (%)
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = totalProfit
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return OverviewStats.builder()
                .totalRevenue(totalRevenue.setScale(0, RoundingMode.DOWN))
                .totalCost(totalCost.setScale(0, RoundingMode.DOWN))
                .totalProfit(totalProfit.setScale(0, RoundingMode.DOWN))
                .profitMargin(profitMargin)
                .totalOrders(payments.size())
                .totalProducts(totalProducts)
                .build();
    }

    /**
     * Tính chi tiết doanh thu từng đơn hàng
     */
    private List<OrderRevenueDetail> calculateOrderDetails(List<Payment> payments) {
        List<OrderRevenueDetail> details = new ArrayList<>();
        
        for (Payment payment : payments) {
            Order order = payment.getOrder();
            
            BigDecimal orderCost = BigDecimal.ZERO;
            List<ProductRevenueDetail> productDetails = new ArrayList<>();
            
            // Tính chi tiết từng sản phẩm trong đơn
            for (OrderItem item : order.getItems()) {
                Product product = item.getProductVariant().getProduct();
                
                BigDecimal costPrice = product.getCostPrice() != null 
                    ? product.getCostPrice() 
                    : item.getUnitPrice();
                
                BigDecimal itemRevenue = item.getTotalPrice();
                BigDecimal itemCost = costPrice.multiply(new BigDecimal(item.getQuantity()));
                BigDecimal itemProfit = itemRevenue.subtract(itemCost);
                
                orderCost = orderCost.add(itemCost);
                
                productDetails.add(ProductRevenueDetail.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .sku(product.getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice().setScale(0, RoundingMode.DOWN))
                        .costPrice(costPrice.setScale(0, RoundingMode.DOWN))
                        .revenue(itemRevenue.setScale(0, RoundingMode.DOWN))
                        .cost(itemCost.setScale(0, RoundingMode.DOWN))
                        .profit(itemProfit.setScale(0, RoundingMode.DOWN))
                        .build());
            }
            
            BigDecimal orderRevenue = payment.getAmount();
            BigDecimal orderProfit = orderRevenue.subtract(orderCost);
            
            details.add(OrderRevenueDetail.builder()
                    .orderId(order.getId())
                    .orderNumber("DH" + String.format("%06d", order.getId()))
                    .orderDate(order.getCreatedAt())
                    .orderRevenue(orderRevenue.setScale(0, RoundingMode.DOWN))
                    .orderCost(orderCost.setScale(0, RoundingMode.DOWN))
                    .orderProfit(orderProfit.setScale(0, RoundingMode.DOWN))
                    .customerName(order.getUser().getFullName())
                    .customerEmail(order.getUser().getEmail())
                    .products(productDetails)
                    .build());
        }
        
        return details;
    }

    /**
     * Thống kê theo khoảng thời gian
     */
    private TimeRangeStats calculateTimeRangeStats(
            List<Payment> payments, 
            LocalDateTime fromDate, 
            LocalDateTime toDate) {
        
        BigDecimal periodRevenue = BigDecimal.ZERO;
        BigDecimal periodCost = BigDecimal.ZERO;
        
        for (Payment payment : payments) {
            periodRevenue = periodRevenue.add(payment.getAmount());
            
            for (OrderItem item : payment.getOrder().getItems()) {
                Product product = item.getProductVariant().getProduct();
                BigDecimal costPrice = product.getCostPrice() != null 
                    ? product.getCostPrice() 
                    : item.getUnitPrice();
                
                periodCost = periodCost.add(
                    costPrice.multiply(new BigDecimal(item.getQuantity()))
                );
            }
        }
        
        BigDecimal periodProfit = periodRevenue.subtract(periodCost);
        
        return TimeRangeStats.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .periodRevenue(periodRevenue.setScale(0, RoundingMode.DOWN))
                .periodCost(periodCost.setScale(0, RoundingMode.DOWN))
                .periodProfit(periodProfit.setScale(0, RoundingMode.DOWN))
                .periodOrders(payments.size())
                .build();
    }

    /**
     * Thống kê theo ngày (daily statistics)
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getDailyRevenue(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        return payments.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getCreatedAt().toLocalDate().toString(),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Payment::getAmount,
                        BigDecimal::add
                    )
                ));
    }

    /**
     * Thống kê theo tháng (monthly statistics)
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMonthlyRevenue(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        return payments.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getCreatedAt().getYear() + "-" + 
                         String.format("%02d", p.getCreatedAt().getMonthValue()),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Payment::getAmount,
                        BigDecimal::add
                    )
                ));
    }
}