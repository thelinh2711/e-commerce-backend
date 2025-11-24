package com.example.shop_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsRequest {
    
    /**
     * Ngày bắt đầu (yyyy-MM-dd)
     * - Với DATE_RANGE: bắt buộc
     * - Với WEEK: không bắt buộc, mặc định tuần hiện tại
     * - Với MONTH: không bắt buộc, mặc định tháng hiện tại
     * - Với YEAR: không bắt buộc, mặc định năm hiện tại
     */
    private LocalDate fromDate;
    
    /**
     * Ngày kết thúc (yyyy-MM-dd)
     * - Với DATE_RANGE: bắt buộc
     * - Với WEEK/MONTH/YEAR: tự động tính
     */
    private LocalDate toDate;
}