package com.landriskai.api.dto;

import com.landriskai.domain.RiskBand;
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
public class ReportSummaryResponse {
    private Long reportId;
    private Long orderId;
    private RiskBand riskBand;
    private Integer riskScore;
    private Instant generatedAt;
    private String downloadUrl;
    private Instant expiresAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ParcelSnapshot {
    private String district;
    private String circle;
    private String village;
    private String khata;
    private String khesra;
    private String ownerName;
    private String plotArea;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RiskFindingDTO {
    private String title;
    private String message;
    private String severity;
    private String evidence;
    private String source;
    private Double confidence;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class NextStep {
    private String action;
    private String description;
}
