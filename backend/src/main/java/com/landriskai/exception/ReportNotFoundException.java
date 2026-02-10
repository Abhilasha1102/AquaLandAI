package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when report is not found
 */
public class ReportNotFoundException extends LandRiskAiException {
    public ReportNotFoundException(Long reportId) {
        super("REPORT_NOT_FOUND", "Report not found: " + reportId, HttpStatus.NOT_FOUND.value());
    }
}
