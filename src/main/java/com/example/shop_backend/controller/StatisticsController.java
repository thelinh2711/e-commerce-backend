package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.StatisticsRequest;
import com.example.shop_backend.dto.response.ApiResponse;
import com.example.shop_backend.dto.response.StatisticsResponse;
import com.example.shop_backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // =====================================================
    // THỐNG KÊ DOANH THU - ADMIN & OWNER
    // =====================================================

    /**
     * 1. Thống kê doanh thu từ ngày đến ngày
     * GET /api/statistics/revenue/date-range?fromDate=2025-01-01&toDate=2025-01-31
     */
    @GetMapping("/revenue/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        
        StatisticsResponse response = statisticsService.getRevenueByDateRange(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê doanh thu theo khoảng thời gian", response));
    }

    /**
     * 2. Thống kê doanh thu theo tuần
     * GET /api/statistics/revenue/week?date=2025-01-15
     * (Nếu không truyền date, lấy tuần hiện tại)
     */
    @GetMapping("/revenue/week")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getRevenueByWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getRevenueByWeek(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê doanh thu theo tuần", response));
    }

    /**
     * 3. Thống kê doanh thu theo tháng
     * GET /api/statistics/revenue/month?date=2025-01-15
     * (Nếu không truyền date, lấy tháng hiện tại)
     */
    @GetMapping("/revenue/month")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getRevenueByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getRevenueByMonth(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê doanh thu theo tháng", response));
    }

    /**
     * 4. Thống kê doanh thu theo năm
     * GET /api/statistics/revenue/year?date=2025-01-15
     * (Nếu không truyền date, lấy năm hiện tại)
     */
    @GetMapping("/revenue/year")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getRevenueByYear(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getRevenueByYear(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê doanh thu theo năm", response));
    }

    // =====================================================
    // THỐNG KÊ LỢI NHUẬN - CHỈ OWNER
    // =====================================================

    /**
     * 5. Thống kê lợi nhuận từ ngày đến ngày
     * GET /api/statistics/profit/date-range?fromDate=2025-01-01&toDate=2025-01-31
     */
    @GetMapping("/profit/date-range")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getProfitByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        
        StatisticsResponse response = statisticsService.getProfitByDateRange(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê lợi nhuận theo khoảng thời gian", response));
    }

    /**
     * 6. Thống kê lợi nhuận theo tuần
     * GET /api/statistics/profit/week?date=2025-01-15
     */
    @GetMapping("/profit/week")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getProfitByWeek(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getProfitByWeek(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê lợi nhuận theo tuần", response));
    }

    /**
     * 7. Thống kê lợi nhuận theo tháng
     * GET /api/statistics/profit/month?date=2025-01-15
     */
    @GetMapping("/profit/month")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getProfitByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getProfitByMonth(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê lợi nhuận theo tháng", response));
    }

    /**
     * 8. Thống kê lợi nhuận theo năm
     * GET /api/statistics/profit/year?date=2025-01-15
     */
    @GetMapping("/profit/year")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getProfitByYear(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        StatisticsRequest request = StatisticsRequest.builder()
                .fromDate(date != null ? date : LocalDate.now())
                .build();
        
        StatisticsResponse response = statisticsService.getProfitByYear(request);
        return ResponseEntity.ok(ApiResponse.success("Thống kê lợi nhuận theo năm", response));
    }
}