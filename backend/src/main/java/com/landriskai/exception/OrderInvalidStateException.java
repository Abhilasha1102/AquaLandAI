package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when order is in invalid state for requested operation
 */
public class OrderInvalidStateException extends LandRiskAiException {
    public OrderInvalidStateException(Long orderId, String currentStatus) {
        super("ORDER_INVALID_STATE", 
              "Order " + orderId + " is in state " + currentStatus + " and cannot be processed", 
              HttpStatus.CONFLICT.value());
    }
}
