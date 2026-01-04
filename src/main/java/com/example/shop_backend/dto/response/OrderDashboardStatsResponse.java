package com.example.shop_backend.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDashboardStatsResponse {
    private long total;
    private long pending;
    private long confirmed;
    private long shipped;
    private long delivered;
}
