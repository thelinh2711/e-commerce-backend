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

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class RevenueStatisticsController {

    private final RevenueStatisticsService statisticsService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
     * Lấy thống kê doanh thu với chi tiết đơn hàng
     * Hỗ trợ phân trang qua paymentId
     * 
     * GET /api/statistics/revenue
     * 
     * Query params:
     * - fromDate: Ngày bắt đầu (yyyy-MM-dd)
     * - toDate: Ngày kết thúc (yyyy-MM-dd)
     * - paymentId: ID payment để bắt đầu lấy (phân trang)
     * - limit: Số lượng bản ghi tối đa (mặc định 30)
     * 
     * Ví dụ:
     * 1. Lấy 30 payment đầu tiên: GET /api/statistics/revenue?fromDate=2023-10-01&toDate=2023-10-15
     * 2. Lấy 30 payment tiếp theo: GET /api/statistics/revenue?fromDate=2023-10-01&toDate=2023-10-15&paymentId=30
     * 
     * Response: Bao gồm overview và orderDetails với thông tin variant
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse.RevenueOnlyStats>> getRevenueOnly(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer paymentId,
            @RequestParam(required = false, defaultValue = "30") Integer limit) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        RevenueStatisticsRequest request = RevenueStatisticsRequest.builder()
                .fromDate(parsedFromDate)
                .toDate(parsedToDate)
                .paymentId(paymentId)
                .limit(limit)
                .build();

        RevenueStatisticsResponse.RevenueOnlyStats stats = statisticsService.getRevenueOnly(request);

        return ResponseEntity.ok(ApiResponse.<RevenueStatisticsResponse.RevenueOnlyStats>builder()
                .code(1000)
                .message("Lấy thống kê doanh thu thành công")
                .result(stats)
                .build());
    }

    // =====================================================
    // LỢI NHUẬN - CHỈ OWNER có thể xem
    // =====================================================

    /**
     * Lấy thống kê đầy đủ (doanh thu + lợi nhuận) với chi tiết đơn hàng
     * CHỈ OWNER được phép truy cập
     * Hỗ trợ phân trang qua paymentId
     * 
     * GET /api/statistics/profit
     * 
     * Query params tương tự /revenue
     * Ví dụ:
     * 1. Lấy 30 payment đầu tiên: GET /api/statistics/profit?fromDate=2023-10-01&toDate=2023-10-15
     * 2. Lấy 30 payment tiếp theo: GET /api/statistics/profit?fromDate=2023-10-01&toDate=2023-10-15&paymentId=30
     */
    @GetMapping("/profit")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse>> getProfitStatistics(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer paymentId,
            @RequestParam(required = false, defaultValue = "30") Integer limit) {

        LocalDateTime parsedFromDate = parseFromDate(fromDate);
        LocalDateTime parsedToDate = parseToDate(toDate);

        RevenueStatisticsRequest request = RevenueStatisticsRequest.builder()
                .fromDate(parsedFromDate)
                .toDate(parsedToDate)
                .paymentId(paymentId)
                .limit(limit)
                .build();

        RevenueStatisticsResponse statistics = statisticsService.getRevenueStatistics(request);

        return ResponseEntity.ok(ApiResponse.<RevenueStatisticsResponse>builder()
                .code(1000)
                .message("Lấy thống kê lợi nhuận thành công")
                .result(statistics)
                .build());
    }
}