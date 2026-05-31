package com.caju.transactionauthorizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.caju.transactionauthorizer.model.TransactionModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Verifies that validation errors and unexpected exceptions are mapped
 * to the correct HTTP status codes and structured error responses.</p>
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleValidation should return 400 with VALIDATION_FAILED code and field error details")
    void shouldReturn400WithValidationFailedCode() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TransactionModel("1", java.math.BigDecimal.ONE, "1", "m"), "transactionModel");
        bindingResult.addError(new FieldError("transactionModel", "account", "Account is required"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().code());
        assertTrue(response.getBody().message().contains("account"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleGeneric should return 500 with INTERNAL_SERVER_ERROR code for unexpected exceptions")
    void shouldReturn500WithInternalServerErrorCode() {
        Exception exception = new RuntimeException("Something went wrong");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGeneric(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
        assertTrue(response.getBody().message().contains("unexpected"));
        assertNotNull(response.getBody().timestamp());
    }
}
