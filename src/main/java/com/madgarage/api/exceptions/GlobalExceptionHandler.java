package com.madgarage.api.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global API Exception Handler.
 * DEACTIVATED: Logic merged into com.madgarage.api.config.GlobalExceptionHandler to resolve bean conflicts.
 */
// @ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimit(RateLimitExceededException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("retryAfterSeconds", ex.getWaitTimeSeconds());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", String.valueOf(ex.getWaitTimeSeconds()));
        
        return new ResponseEntity<>(body, headers, HttpStatus.TOO_MANY_REQUESTS);
    }
}
