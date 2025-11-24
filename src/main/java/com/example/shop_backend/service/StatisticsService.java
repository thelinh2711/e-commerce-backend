package com.example.shop_backend.service;

import com.example.shop_backend.dto.request.StatisticsRequest;
import com.example.shop_backend.dto.response.StatisticsResponse;
import com.example.shop_backend.model.Order;
import com.example.shop_backend.model.OrderItem;
import com.example.shop_backend.model.Payment;
import com.example.shop_backend.model.enums.PaymentStatus;
import com.example.shop_backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PaymentRepository paymentRepository;

    // =====================================================
    // DOANH THU
    // =====================================================

    @Transactional(readOnly = true)
    public StatisticsResponse getRevenueByDateRange(StatisticsRequest request) {
        return calculateStatistics(request.getFromDate(), request.getToDate(), false, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getRevenueByWeek(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        
        return calculateStatistics(startOfWeek, endOfWeek, false, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getRevenueByMonth(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        
        return calculateStatistics(startOfMonth, endOfMonth, false, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getRevenueByYear(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfYear = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        
        return calculateStatistics(startOfYear, endOfYear, false, "month");
    }

    // =====================================================
    // LỢI NHUẬN
    // =====================================================

    @Transactional(readOnly = true)
    public StatisticsResponse getProfitByDateRange(StatisticsRequest request) {
        return calculateStatistics(request.getFromDate(), request.getToDate(), true, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getProfitByWeek(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        
        return calculateStatistics(startOfWeek, endOfWeek, true, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getProfitByMonth(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        
        return calculateStatistics(startOfMonth, endOfMonth, true, "day");
    }

    @Transactional(readOnly = true)
    public StatisticsResponse getProfitByYear(StatisticsRequest request) {
        LocalDate date = request.getFromDate();
        LocalDate startOfYear = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        
        return calculateStatistics(startOfYear, endOfYear, true, "month");
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    private StatisticsResponse calculateStatistics(
            LocalDate fromDate, 
            LocalDate toDate, 
            boolean includeProfit, 
            String groupBy) {
        
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
        
        // Lấy tất cả payments đã thanh toán trong khoảng thời gian
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.PAID).stream()
                .filter(p -> !p.getCreatedAt().isBefore(fromDateTime) && 
                           !p.getCreatedAt().isAfter(toDateTime))
                .collect(Collectors.toList());
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalOrders = payments.size();
        int totalProducts = 0;
        
        // Map để nhóm theo ngày/tháng
        Map<String, DailyData> dailyDataMap = new LinkedHashMap<>();
        
        for (Payment payment : payments) {
            Order order = payment.getOrder();
            String dateKey = getDateKey(payment.getCreatedAt(), groupBy);
            
            // Khởi tạo DailyData nếu chưa có
            dailyDataMap.putIfAbsent(dateKey, new DailyData());
            DailyData dailyData = dailyDataMap.get(dateKey);
            
            // Cộng dồn doanh thu
            BigDecimal orderRevenue = payment.getAmount();
            totalRevenue = totalRevenue.add(orderRevenue);
            dailyData.orderCount++;
            
            // Tính cost và số sản phẩm
            BigDecimal orderCost = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                totalProducts += item.getQuantity();
                dailyData.productCount += item.getQuantity();
                
                BigDecimal costPrice = item.getProductVariant().getProduct().getCostPrice();
                if (costPrice == null) {
                    costPrice = item.getUnitPrice();
                }
                BigDecimal itemCost = costPrice.multiply(new BigDecimal(item.getQuantity()));
                totalCost = totalCost.add(itemCost);
                orderCost = orderCost.add(itemCost);
            }
            
            // Luôn lưu revenue và cost riêng biệt
            dailyData.revenue = dailyData.revenue.add(orderRevenue);
            if (includeProfit) {
                dailyData.cost = dailyData.cost.add(orderCost);
            }
        }
        
        // Tạo danh sách chi tiết
        List<StatisticsResponse.DailyStatistic> details = dailyDataMap.entrySet().stream()
                .map(entry -> {
                    DailyData data = entry.getValue();
                    BigDecimal profit = includeProfit ? data.revenue.subtract(data.cost) : null;
                    
                    return StatisticsResponse.DailyStatistic.builder()
                            .date(entry.getKey())
                            .revenue(data.revenue.setScale(0, RoundingMode.DOWN))
                            .cost(includeProfit ? data.cost.setScale(0, RoundingMode.DOWN) : null)
                            .profit(includeProfit ? profit.setScale(0, RoundingMode.DOWN) : null)
                            .orderCount(data.orderCount)
                            .productCount(data.productCount)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Tính tổng profit
        BigDecimal totalProfit = includeProfit ? totalRevenue.subtract(totalCost) : null;
        
        return StatisticsResponse.builder()
                .totalRevenue(totalRevenue.setScale(0, RoundingMode.DOWN))
                .totalCost(includeProfit ? totalCost.setScale(0, RoundingMode.DOWN) : null)
                .totalProfit(includeProfit ? totalProfit.setScale(0, RoundingMode.DOWN) : null)
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .fromDate(fromDate)
                .toDate(toDate)
                .details(details)
                .build();
    }
    
    private String getDateKey(LocalDateTime dateTime, String groupBy) {
        if ("month".equals(groupBy)) {
            return String.format("%d-%02d", dateTime.getYear(), dateTime.getMonthValue());
        }
        return dateTime.toLocalDate().toString(); // yyyy-MM-dd
    }
    
    // Class để lưu dữ liệu tạm thời
    private static class DailyData {
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
        int orderCount = 0;
        int productCount = 0;
    }
}