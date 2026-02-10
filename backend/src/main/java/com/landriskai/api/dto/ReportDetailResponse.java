package com.landriskai.api.dto;

import com.landriskai.domain.RiskBand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailResponse {
    private Long reportId;
    private Long orderId;
    private RiskBand riskBand;
    private Integer riskScore;
    private String verificationCode;
    private Instant generatedAt;
    private Instant expiresAt;
    private String downloadUrl;
    private String verifyUrl;
    private List<Map<String, Object>> findings;
    private Map<String, Object> parcelSnapshot;
}

