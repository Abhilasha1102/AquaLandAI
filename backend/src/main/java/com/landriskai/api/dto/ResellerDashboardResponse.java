package com.landriskai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResellerDashboardResponse {
    private Long resellerId;
    private String resellerName;
    private String resellerCode;
    private Long totalReportsGenerated;
    private Long totalEarningsInPaise;
    private Long currentWalletBalanceInPaise;
    private String commissionMode;
    private List<RecentTransaction> recentTransactions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private Long orderId;
        private Long reportId;
        private Long commissionPaise;
        private Instant timestamp;
    }
}
