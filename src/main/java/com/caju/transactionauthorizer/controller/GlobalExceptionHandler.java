package com.caju.transactionauthorizer.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler providing consistent, structured error responses
 * for all REST controllers.
 *
 * <p>Note: per the challenge specification, the transaction endpoint itself never
 * throws — all authorization errors are returned as response codes ({@code "07"}).
 * This handler covers infrastructure concerns like Bean Validation failures and
 * unexpected runtime exceptions that escape the service layer.</p>
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
     * Returns {@code 400 BAD REQUEST} with a summary of field validation errors.
     *
     * @param exception the thrown {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} with {@code 400} and validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "; " + b);

        log.warn("Validation failed. errors={}", details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_FAILED", details, LocalDateTime.now()));
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
     * @param code      machine-readable error code (e.g., {@code VALIDATION_FAILED})
     * @param message   human-readable description of the error
     * @param timestamp UTC timestamp of when the error occurred
     */
    public record ErrorResponse(String code, String message, LocalDateTime timestamp) {
    }
}
