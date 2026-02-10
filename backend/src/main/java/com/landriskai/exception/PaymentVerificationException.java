package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when payment verification fails
 */
public class PaymentVerificationException extends LandRiskAiException {
    public PaymentVerificationException(String message) {
        super("PAYMENT_VERIFICATION_FAILED", message, HttpStatus.BAD_REQUEST.value());
    }
}
