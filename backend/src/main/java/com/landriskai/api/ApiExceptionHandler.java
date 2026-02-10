package com.landriskai.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(err("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(err("VALIDATION_ERROR", msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> server(Exception ex) {
        return ResponseEntity.internalServerError().body(err("SERVER_ERROR", ex.getMessage()));
    }

    private Map<String, Object> err(String code, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", code);
        m.put("message", message);
        return m;
    }
}
