package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when verification code is invalid or expired
 */
public class VerificationCodeInvalidException extends LandRiskAiException {
    public VerificationCodeInvalidException(String message) {
        super("VERIFICATION_CODE_INVALID", message, HttpStatus.UNAUTHORIZED.value());
    }
}
