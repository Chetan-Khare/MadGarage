package com.madgarage.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.madgarage.api.exceptions.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler — the safety net for all unhandled exceptions.
 * Expanded to cover:
 *   - Bean validation failures (400)
 *   - ResponseStatusException (propagated from service layer with specific status codes)
 *   - SQL constraint violations like duplicate email (409)
 *   - Spring Security AuthorizationDeniedException (403) — must be explicit or catch-all returns 500
 *   - All other uncaught exceptions (500) — logs full trace server-side, returns clean message to client
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles @Valid bean validation failures (e.g., @NotBlank, @Email).
     * Returns a field-level error map so the frontend can highlight the correct input.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles ResponseStatusException thrown from service layer.
     * Preserves the HTTP status code and message set by the service.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason() != null ? ex.getReason() : ex.getMessage()));
    }

    /**
     * Handles Rate Limit violations by communicating wait times to the client via Retry-After.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimit(RateLimitExceededException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("retryAfterSeconds", ex.getWaitTimeSeconds());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", String.valueOf(ex.getWaitTimeSeconds()));
        
        return new ResponseEntity<>(body, headers, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handles multipart upload limit failures (Size or File Count).
     * Returns 400 Bad Request with a clear message to the user.
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceeded(org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
        log.warn("Upload limit exceeded: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Upload limit exceeded. Please reduce the number of images or file size."));
    }

    /**
     * Handles SQL unique constraint violations (e.g., duplicate email on register).
     * Returns 409 Conflict with a safe message — never exposes raw SQL details.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(409)
                .body(Map.of("error", "A record with this value already exists."));
    }

    /**
     * Handles Spring Security method-level @PreAuthorize denials.
     * Without this, AuthorizationDeniedException falls through to the 500 catch-all.
     * Returns 403 Forbidden with a clean message.
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("[Security] Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied. You do not have permission to perform this action."));
    }

    /**
     * Catch-all safety net for any unhandled exception.
     * Logs the full stack trace server-side only — NEVER sent to the client.
     * Returns a generic 500 message to prevent internal detail leakage.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception ex) {
        log.error("Unhandled exception caught by GlobalExceptionHandler: ", ex);
        return ResponseEntity.status(500)
                .body(Map.of("error", "An unexpected error occurred. Please try again later."));
    }

    /**
     * Suppress stack trace for 404 static resource requests (e.g. /favicon.ico)
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
