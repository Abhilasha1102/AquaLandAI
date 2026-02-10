package com.landriskai.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when external service integration fails (data fetch, WhatsApp, etc.)
 */
public class ExternalServiceException extends LandRiskAiException {
    public ExternalServiceException(String serviceName, String message) {
        super("EXTERNAL_SERVICE_ERROR", 
              "Failed to reach " + serviceName + ": " + message, 
              HttpStatus.SERVICE_UNAVAILABLE.value());
    }
}
