package com.landriskai.risk;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskFinding {
    private String code;
    private String title;
    private String message;
    private FindingSeverity severity;
    private String evidence;
    private String source;
    private String confidence; // HIGH/MEDIUM/LOW for MVP
}
