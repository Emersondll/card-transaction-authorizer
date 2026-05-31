package com.caju.transactionauthorizer.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler providing consistent, structured error responses
 * for all REST controllers.
 *
 * <p>Centralises error handling to prevent sensitive information leakage and
 * ensure uniform error contracts across all API endpoints.</p>
 *
 * <p>Error response format (generic errors):
 * <pre>{@code
 * { "code": "INTERNAL_SERVER_ERROR", "message": "...", "timestamp": "2026-01-01T00:00:00" }
 * }</pre>
 *
 * <p>Error response format (validation errors):
 * <pre>{@code
 * { "code": "VALIDATION_FAILED", "message": "...",
 *   "errors": { "field": "reason" }, "timestamp": "2026-01-01T00:00:00" }
 * }</pre>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures from {@code @Valid} on request bodies.
     * Returns {@code 400 BAD REQUEST} with a map of field → error message.
     *
     * @param exception the thrown {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} with {@code 400} and structured field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = exception.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (first, second) -> first
                ));

        log.warn("Validation failed. errors={}", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(
                        "VALIDATION_FAILED",
                        "Input validation failed",
                        fieldErrors,
                        LocalDateTime.now()
                ));
    }

    /**
     * Fallback handler for any unexpected exception not handled elsewhere.
     * Returns {@code 500 INTERNAL SERVER ERROR} without exposing internal details.
     *
     * @param exception the unexpected exception
     * @return {@link ResponseEntity} with {@code 500} and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception exception) {
        log.error("Unexpected error occurred", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()
                ));
    }

    /**
     * Immutable record representing a structured API error response.
     *
     * @param code      machine-readable error code (e.g., {@code INTERNAL_SERVER_ERROR})
     * @param message   human-readable description of the error
     * @param timestamp UTC timestamp of when the error occurred
     */
    public record ErrorResponse(String code, String message, LocalDateTime timestamp) {
    }

    /**
     * Immutable record representing a validation error response with per-field details.
     *
     * @param code      always {@code VALIDATION_FAILED}
     * @param message   summary message
     * @param errors    map of field name → validation error message
     * @param timestamp UTC timestamp of when the error occurred
     */
    public record ValidationErrorResponse(
            String code,
            String message,
            Map<String, String> errors,
            LocalDateTime timestamp
    ) {
    }
}
