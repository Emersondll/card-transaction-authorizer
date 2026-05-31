package com.caju.transactionauthorizer.controller;

import java.math.BigDecimal;

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
 */
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleValidation should return 400 with VALIDATION_FAILED and per-field error map")
    void shouldReturn400WithValidationFailedAndFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new TransactionModel("1", BigDecimal.ONE, "1", "m"), "transactionModel");
        bindingResult.addError(new FieldError("transactionModel", "account", "Account is required"));
        bindingResult.addError(new FieldError("transactionModel", "mcc", "MCC is required"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_FAILED", response.getBody().code());
        assertTrue(response.getBody().errors().containsKey("account"));
        assertTrue(response.getBody().errors().containsKey("mcc"));
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleValidation should use first message when same field has multiple errors")
    void shouldUseFirstMessageWhenSameFieldHasMultipleErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new TransactionModel("1", BigDecimal.ONE, "1", "m"), "transactionModel");
        bindingResult.addError(new FieldError("transactionModel", "account", "Account is required"));
        bindingResult.addError(new FieldError("transactionModel", "account", "Account must not be blank"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().errors().size()); // merged duplicate key
        assertEquals("VALIDATION_FAILED", response.getBody().code());
    }

    @Test
    @DisplayName("handleGeneric should return 500 with INTERNAL_SERVER_ERROR for unexpected exceptions")
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
