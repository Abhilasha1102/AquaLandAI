package com.landriskai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMetricsResponse {
    private Integer totalOrders;
    private Integer totalReports;
    private Integer totalUsers;
    private Long totalRevenueInPaise;
    private Double avgProcessingTimeMs;
    private List<DistrictStats> byDistrict;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictStats {
        private String district;
        private Integer orderCount;
        private Long totalRevenueInPaise;
    }
}
