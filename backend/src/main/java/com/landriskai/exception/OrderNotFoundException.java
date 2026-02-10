package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when order is not found
 */
public class OrderNotFoundException extends LandRiskAiException {
    public OrderNotFoundException(Long orderId) {
        super("ORDER_NOT_FOUND", "Order not found: " + orderId, HttpStatus.NOT_FOUND.value());
    }

    public OrderNotFoundException(String message) {
        super("ORDER_NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }
}
