package com.example.shop_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticsRequest {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
    