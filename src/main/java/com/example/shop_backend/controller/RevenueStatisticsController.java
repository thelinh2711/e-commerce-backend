package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.RevenueStatisticsRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.RevenueStatisticsResponse;
import com.example.shop_backend.service.RevenueStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
Controller cho thống kê doanh thu và lợi nhuận
- ADMIN và OWNER có thể xem doanh thu
- CHỈ OWNER có thể xem lợi nhuận
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class RevenueStatisticsController {

    private final RevenueStatisticsService statisticsService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
    Parse date string và tự động set giờ
    - fromDate: set 00:00:00
    - toDate: set 23:59:59
     */
    private LocalDateTime parseFromDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            return date.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseToDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            return date.atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }

    // =====================================================
    // DOANH THU - ADMIN & OWNER có thể xem
    // =====================================================

    /**
    Lấy thống kê doanh thu (KHÔNG bao gồm lợi nhuận)
    
    GET /api/statistics/revenue
    
    Query params:
    - fromDate: Ngày bắt đầu (yyyy-MM-dd)
    - toDate: Ngày kết thúc (yyyy-MM-dd)
    
    Trả về: Chỉ thông tin doanh thu, không có cost/profit
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse.RevenueOnlyStats>> getRevenueOnly(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        RevenueStatisticsRequest request = RevenueStatisticsRequest.builder()
                .fromDate(parsedFromDate)
                .toDate(parsedToDate)
                .build();

        RevenueStatisticsResponse.RevenueOnlyStats stats = statisticsService.getRevenueOnly(request);

        return ResponseEntity.ok(ApiResponse.<RevenueStatisticsResponse.RevenueOnlyStats>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu thành công")
                .result(stats)
                .build());
    }

    /**
    Thống kê doanh thu theo ngày
    
    GET /api/statistics/revenue/daily
     */
    @GetMapping("/revenue/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getDailyRevenue(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> dailyRevenue = statisticsService.getDailyRevenue(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu theo ngày thành công")
                .result(dailyRevenue)
                .build());
    }

    /**
    Thống kê doanh thu theo tháng
    
    GET /api/statistics/revenue/monthly
     */
    @GetMapping("/revenue/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMonthlyRevenue(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> monthlyRevenue = statisticsService.getMonthlyRevenue(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu theo tháng thành công")
                .result(monthlyRevenue)
                .build());
    }

    /**
    Thống kê doanh thu theo quý
    
    GET /api/statistics/revenue/quarterly
     */
    @GetMapping("/revenue/quarterly")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getQuarterlyRevenue(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> quarterlyRevenue = statisticsService.getQuarterlyRevenue(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu theo quý thành công")
                .result(quarterlyRevenue)
                .build());
    }

    /**
    Thống kê doanh thu theo năm
    
    GET /api/statistics/revenue/yearly
     */
    @GetMapping("/revenue/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getYearlyRevenue(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> yearlyRevenue = statisticsService.getYearlyRevenue(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu theo năm thành công")
                .result(yearlyRevenue)
                .build());
    }

    // =====================================================
    // LỢI NHUẬN - CHỈ OWNER có thể xem
    // =====================================================

    /**
    Lấy thống kê đầy đủ (doanh thu + lợi nhuận)
    CHỈ OWNER được phép truy cập
    
    GET /api/statistics/profit
     */
    @GetMapping("/profit")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse>> getProfitStatistics(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        RevenueStatisticsRequest request = RevenueStatisticsRequest.builder()
                .fromDate(parsedFromDate)
                .toDate(parsedToDate)
                .build();

        RevenueStatisticsResponse statistics = statisticsService.getRevenueStatistics(request);

        return ResponseEntity.ok(ApiResponse.<RevenueStatisticsResponse>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận thành công")
                .result(statistics)
                .build());
    }

    /**
    Thống kê lợi nhuận theo ngày
    CHỈ OWNER được phép
    
    GET /api/statistics/profit/daily
     */
    @GetMapping("/profit/daily")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getDailyProfit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> dailyProfit = statisticsService.getDailyProfit(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận theo ngày thành công")
                .result(dailyProfit)
                .build());
    }

    /**
    Thống kê lợi nhuận theo tháng
    CHỈ OWNER được phép
    
    GET /api/statistics/profit/monthly
     */
    @GetMapping("/profit/monthly")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getMonthlyProfit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> monthlyProfit = statisticsService.getMonthlyProfit(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận theo tháng thành công")
                .result(monthlyProfit)
                .build());
    }

    /**
    Thống kê lợi nhuận theo quý
    CHỈ OWNER được phép
    
    GET /api/statistics/profit/quarterly
     */
    @GetMapping("/profit/quarterly")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getQuarterlyProfit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> quarterlyProfit = statisticsService.getQuarterlyProfit(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận theo quý thành công")
                .result(quarterlyProfit)
                .build());
    }

    /**
    Thống kê lợi nhuận theo năm
    CHỈ OWNER được phép
    
    GET /api/statistics/profit/yearly
     */
    @GetMapping("/profit/yearly")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getYearlyProfit(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        Map<String, BigDecimal> yearlyProfit = statisticsService.getYearlyProfit(parsedFromDate, parsedToDate);

        return ResponseEntity.ok(ApiResponse.<Map<String, BigDecimal>>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận theo năm thành công")
                .result(yearlyProfit)
                .build());
    }
}


/*
    Thống kê doanh thu từ ngày đến ngày
    json:
    {
    "fromDate": "2023-10-01",
    "toDate": "2023-10-15"
    }

    response:
    {
      "overview": {
            "totalRevenue": 13721000,
            "totalOrders": 6,
            "totalProducts": 7
        },
        "orderDetails": [
            {
                "orderId": 8,
                "orderNumber": "DH000008",
                "orderDate": "2025-10-10T08:00:00",
                "orderRevenue": 2800000,
                "customerName": "Bùi Văn Hải",
                "customerEmail": "hai.bui@gmail.com",
                "OrderStatus" : "DELIVERED",
                "PaymentMethod": "BANK_TRANSFER",
                "products": [
                    {
                        "productId": 21,
                        "productName": "Puma Velocity Nitro 2",
                        "sku": "SP21",
                        "revenue": 2800000
                        "product_variant": {
                            "variantId": 31,
                            "quantity": 1,
                            "color": "Red",
                            "size": "42"
                        }
                    }
                ]
            },
       ]
  }
 */