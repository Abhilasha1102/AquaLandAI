package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when input validation fails
 */
public class InputValidationException extends LandRiskAiException {
    public InputValidationException(String field, String message) {
        super("VALIDATION_ERROR", "Field '" + field + "': " + message, HttpStatus.BAD_REQUEST.value());
    }
}
