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
 * Controller cho thống kê doanh thu và lợi nhuận
 * CHỈ OWNER MỚI CÓ QUYỀN TRUY CẬP
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class RevenueStatisticsController {

    private final RevenueStatisticsService statisticsService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Parse date string và tự động set giờ
     * - fromDate: set 00:00:00
     * - toDate: set 23:59:59
     */
    private LocalDateTime parseFromDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            return date.atStartOfDay(); // 00:00:00
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
            return date.atTime(23, 59, 59); // 23:59:59
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lấy thống kê tổng quan doanh thu và lợi nhuận
     * 
     * GET /api/statistics/revenue
     * 
     * Query params:
     * - fromDate: Ngày bắt đầu (yyyy-MM-dd) - tự động set 00:00:00
     * - toDate: Ngày kết thúc (yyyy-MM-dd) - tự động set 23:59:59
     * 
     * Example: /api/statistics/revenue?fromDate=2025-01-01&toDate=2025-12-31
     */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse>> getRevenueStatistics(
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
                .message("Lấy thống kê doanh thu thành công")
                .result(statistics)
                .build());
    }

    /**
     * Thống kê doanh thu theo ngày
     * 
     * GET /api/statistics/revenue/daily
     * 
     * Query params:
     * - fromDate: yyyy-MM-dd (tự động set 00:00:00)
     * - toDate: yyyy-MM-dd (tự động set 23:59:59)
     * 
     * Trả về Map<date, revenue>
     */
    @GetMapping("/revenue/daily")
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
     * Thống kê doanh thu theo tháng
     * 
     * GET /api/statistics/revenue/monthly
     * 
     * Query params:
     * - fromDate: yyyy-MM-dd (tự động set 00:00:00)
     * - toDate: yyyy-MM-dd (tự động set 23:59:59)
     * 
     * Trả về Map<month, revenue>
     */
    @GetMapping("/revenue/monthly")
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
     * Thống kê nhanh - chỉ lấy overview
     * 
     * GET /api/statistics/revenue/quick
     * 
     * Query params:
     * - fromDate: yyyy-MM-dd (tự động set 00:00:00)
     * - toDate: yyyy-MM-dd (tự động set 23:59:59)
     */
    @GetMapping("/revenue/quick")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse.OverviewStats>> getQuickStats(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        RevenueStatisticsRequest request = RevenueStatisticsRequest.builder()
                .fromDate(parsedFromDate)
                .toDate(parsedToDate)
                .build();

        RevenueStatisticsResponse statistics = statisticsService.getRevenueStatistics(request);

        return ResponseEntity.ok(ApiResponse.<RevenueStatisticsResponse.OverviewStats>builder()
                .code(1000)
                .message("Lấy thống kê nhanh thành công")
                .result(statistics.getOverview())
                .build());
    }
}