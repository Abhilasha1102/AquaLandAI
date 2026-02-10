package com.landriskai.config;

import com.landriskai.exception.LandRiskAiException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Centralized exception handler for all REST APIs
 * Provides consistent error response format across the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom LandRiskAi exceptions
     */
    @ExceptionHandler(LandRiskAiException.class)
    public ResponseEntity<ErrorResponse> handleLandRiskAiException(
            LandRiskAiException ex, 
            WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .status(ex.getHttpStatus())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        List<String> errors = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errors.add(error.getDefaultMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Input validation failed")
                .details(errors)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("SERVER_ERROR")
                .message(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Standard error response format
     */
    @Data
    public static class ErrorResponse {
        private String traceId;
        private Instant timestamp;
        private int status;
        private String errorCode;
        private String message;
        private String path;
        private List<String> details;

        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }

        public static class ErrorResponseBuilder {
            private String traceId;
            private Instant timestamp;
            private int status;
            private String errorCode;
            private String message;
            private String path;
            private List<String> details;

            public ErrorResponseBuilder traceId(String traceId) {
                this.traceId = traceId;
                return this;
            }

            public ErrorResponseBuilder timestamp(Instant timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ErrorResponseBuilder status(int status) {
                this.status = status;
                return this;
            }

            public ErrorResponseBuilder errorCode(String errorCode) {
                this.errorCode = errorCode;
                return this;
            }

            public ErrorResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorResponseBuilder path(String path) {
                this.path = path;
                return this;
            }

            public ErrorResponseBuilder details(List<String> details) {
                this.details = details;
                return this;
            }

            public ErrorResponse build() {
                ErrorResponse response = new ErrorResponse();
                response.traceId = this.traceId;
                response.timestamp = this.timestamp;
                response.status = this.status;
                response.errorCode = this.errorCode;
                response.message = this.message;
                response.path = this.path;
                response.details = this.details;
                return response;
            }
        }
    }
}
