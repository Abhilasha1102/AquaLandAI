package com.landriskai.risk;

import com.landriskai.domain.RiskBand;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RiskResult {
    private int score; // 0-100 (higher = riskier)
    private RiskBand band;
    private List<RiskFinding> findings;
}
