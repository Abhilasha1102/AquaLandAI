package com.landriskai.exception;

/**
 * Base exception for LandRiskAI application
 */
public class LandRiskAiException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public LandRiskAiException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public LandRiskAiException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
