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

/**
 * Service xử lý thống kê doanh thu và lợi nhuận
 * 
 * Phân quyền:
 * - ADMIN & OWNER: Xem doanh thu (revenue)
 * - CHỈ OWNER: Xem lợi nhuận (profit)
 */
@Service
@RequiredArgsConstructor
public class RevenueStatisticsService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Lấy danh sách payments đã thanh toán theo khoảng thời gian
     * 
     * @param fromDate Ngày bắt đầu (nullable)
     * @param toDate Ngày kết thúc (nullable)
     * @return List payments đã PAID và được sắp xếp theo thời gian giảm dần
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

    // =====================================================
    // DOANH THU - Cho ADMIN và OWNER
    // =====================================================

    /**
     * Lấy thống kê CHỈ doanh thu (không có cost/profit)
     * ADMIN và OWNER có thể xem
     * 
     * @param request RevenueStatisticsRequest chứa fromDate và toDate
     * @return RevenueOnlyStats chỉ chứa thông tin doanh thu
     */
    @Transactional(readOnly = true)
    public RevenueOnlyStats getRevenueOnly(RevenueStatisticsRequest request) {
        LocalDateTime fromDate = request.getFromDate();
        LocalDateTime toDate = request.getToDate();
        
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalProducts = 0;
        
        for (Payment payment : payments) {
            totalRevenue = totalRevenue.add(payment.getAmount());
            
            for (OrderItem item : payment.getOrder().getItems()) {
                totalProducts += item.getQuantity();
            }
        }
        
        return RevenueOnlyStats.builder()
                .totalRevenue(totalRevenue.setScale(0, RoundingMode.DOWN))
                .totalOrders(payments.size())
                .totalProducts(totalProducts)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
    }

    /**
     * Thống kê doanh thu theo ngày
     * Map key: yyyy-MM-dd
     * Map value: Tổng doanh thu trong ngày đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là ngày
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
     * Thống kê doanh thu theo tháng
     * Map key: yyyy-MM (ví dụ: 2025-01)
     * Map value: Tổng doanh thu trong tháng đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là tháng
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

    /**
     * Thống kê doanh thu theo quý
     * Map key: yyyy-Qx (ví dụ: 2025-Q1, 2025-Q2)
     * Map value: Tổng doanh thu trong quý đó
     * 
     * Quý được tính như sau:
     * - Q1: tháng 1, 2, 3
     * - Q2: tháng 4, 5, 6
     * - Q3: tháng 7, 8, 9
     * - Q4: tháng 10, 11, 12
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là quý
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getQuarterlyRevenue(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        return payments.stream()
                .collect(Collectors.groupingBy(
                    p -> {
                        int quarter = (p.getCreatedAt().getMonthValue() - 1) / 3 + 1;
                        return p.getCreatedAt().getYear() + "-Q" + quarter;
                    },
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Payment::getAmount,
                        BigDecimal::add
                    )
                ));
    }

    /**
     * Thống kê doanh thu theo năm
     * Map key: yyyy (ví dụ: 2024, 2025)
     * Map value: Tổng doanh thu trong năm đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là năm
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getYearlyRevenue(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        return payments.stream()
                .collect(Collectors.groupingBy(
                    p -> String.valueOf(p.getCreatedAt().getYear()),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Payment::getAmount,
                        BigDecimal::add
                    )
                ));
    }

    // =====================================================
    // LỢI NHUẬN - CHỈ CHO OWNER
    // =====================================================

    /**
     * Lấy thống kê đầy đủ (doanh thu + lợi nhuận)
     * CHỈ OWNER mới được gọi method này
     * 
     * @param request RevenueStatisticsRequest chứa fromDate và toDate
     * @return RevenueStatisticsResponse đầy đủ thông tin
     */
    @Transactional(readOnly = true)
    public RevenueStatisticsResponse getRevenueStatistics(RevenueStatisticsRequest request) {
        LocalDateTime fromDate = request.getFromDate();
        LocalDateTime toDate = request.getToDate();
        
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        // Tính toán các thống kê
        OverviewStats overview = calculateOverviewStats(payments);
        List<OrderRevenueDetail> orderDetails = calculateOrderDetails(payments);
        TimeRangeStats timeRange = calculateTimeRangeStats(payments, fromDate, toDate);
        
        return RevenueStatisticsResponse.builder()
                .overview(overview)
                .orderDetails(orderDetails)
                .timeRange(timeRange)
                .build();
    }

    /**
     * Tính thống kê tổng quan (overview)
     * Bao gồm: doanh thu, chi phí, lợi nhuận, tỷ suất lợi nhuận
     * 
     * @param payments List payments đã thanh toán
     * @return OverviewStats
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
        
        // Tính lợi nhuận
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
     * Tính chi tiết doanh thu/lợi nhuận từng đơn hàng
     * Bao gồm thông tin chi tiết từng sản phẩm trong đơn
     * 
     * @param payments List payments đã thanh toán
     * @return List<OrderRevenueDetail>
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
     * 
     * @param payments List payments đã thanh toán
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return TimeRangeStats
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
     * Thống kê lợi nhuận theo ngày (CHỈ OWNER)
     * Map key: yyyy-MM-dd
     * Map value: Tổng lợi nhuận trong ngày đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là ngày
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getDailyProfit(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        Map<String, BigDecimal> dailyProfit = new HashMap<>();
        
        for (Payment payment : payments) {
            String date = payment.getCreatedAt().toLocalDate().toString();
            BigDecimal orderProfit = calculateOrderProfit(payment.getOrder());
            
            dailyProfit.merge(date, orderProfit, BigDecimal::add);
        }
        
        return dailyProfit;
    }

    /**
     * Thống kê lợi nhuận theo tháng (CHỈ OWNER)
     * Map key: yyyy-MM
     * Map value: Tổng lợi nhuận trong tháng đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là tháng
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getMonthlyProfit(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        Map<String, BigDecimal> monthlyProfit = new HashMap<>();
        
        for (Payment payment : payments) {
            String month = payment.getCreatedAt().getYear() + "-" + 
                          String.format("%02d", payment.getCreatedAt().getMonthValue());
            BigDecimal orderProfit = calculateOrderProfit(payment.getOrder());
            
            monthlyProfit.merge(month, orderProfit, BigDecimal::add);
        }
        
        return monthlyProfit;
    }

    /**
     * Thống kê lợi nhuận theo quý (CHỈ OWNER)
     * Map key: yyyy-Qx
     * Map value: Tổng lợi nhuận trong quý đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là quý
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getQuarterlyProfit(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        Map<String, BigDecimal> quarterlyProfit = new HashMap<>();
        
        for (Payment payment : payments) {
            int quarter = (payment.getCreatedAt().getMonthValue() - 1) / 3 + 1;
            String quarterKey = payment.getCreatedAt().getYear() + "-Q" + quarter;
            BigDecimal orderProfit = calculateOrderProfit(payment.getOrder());
            
            quarterlyProfit.merge(quarterKey, orderProfit, BigDecimal::add);
        }
        
        return quarterlyProfit;
    }

    /**
     * Thống kê lợi nhuận theo năm (CHỈ OWNER)
     * Map key: yyyy
     * Map value: Tổng lợi nhuận trong năm đó
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Map<String, BigDecimal> với key là năm
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getYearlyProfit(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Payment> payments = getFilteredPayments(fromDate, toDate);
        
        Map<String, BigDecimal> yearlyProfit = new HashMap<>();
        
        for (Payment payment : payments) {
            String year = String.valueOf(payment.getCreatedAt().getYear());
            BigDecimal orderProfit = calculateOrderProfit(payment.getOrder());
            
            yearlyProfit.merge(year, orderProfit, BigDecimal::add);
        }
        
        return yearlyProfit;
    }

    /**
     * Helper method: Tính lợi nhuận của 1 đơn hàng
     * Lợi nhuận = Tổng tiền đơn hàng - Tổng chi phí các sản phẩm
     * 
     * @param order Đơn hàng cần tính
     * @return BigDecimal lợi nhuận của đơn
     */
    private BigDecimal calculateOrderProfit(Order order) {
        BigDecimal orderCost = BigDecimal.ZERO;
        
        for (OrderItem item : order.getItems()) {
            Product product = item.getProductVariant().getProduct();
            
            // Lấy cost_price, nếu null thì dùng unit_price
            BigDecimal costPrice = product.getCostPrice() != null 
                ? product.getCostPrice() 
                : item.getUnitPrice();
            
            orderCost = orderCost.add(
                costPrice.multiply(new BigDecimal(item.getQuantity()))
            );
        }
        
        return order.getTotalAmount().subtract(orderCost);
    }
}