package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when admin operation fails
 */
public class AdminOperationException extends LandRiskAiException {
    public AdminOperationException(String message) {
        super("ADMIN_OPERATION_FAILED", message, HttpStatus.BAD_REQUEST.value());
    }
}
