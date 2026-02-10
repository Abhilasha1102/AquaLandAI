package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when report link has expired
 */
public class ReportLinkExpiredException extends LandRiskAiException {
    public ReportLinkExpiredException(Long reportId) {
        super("REPORT_LINK_EXPIRED", 
              "Report link has expired. Please generate a new report.", 
              HttpStatus.GONE.value());
    }
}
