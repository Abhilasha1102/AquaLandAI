package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when user is not authorized for operation
 */
public class UnauthorizedException extends LandRiskAiException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, HttpStatus.FORBIDDEN.value());
    }
}
