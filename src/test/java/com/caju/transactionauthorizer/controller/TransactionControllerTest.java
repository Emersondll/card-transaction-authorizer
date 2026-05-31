package com.caju.transactionauthorizer.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.caju.transactionauthorizer.enums.TransactionStatusCode;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.service.TransactionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TransactionController}.
 *
 * <p>Verifies HTTP status codes and response body delegation to the service layer.</p>
 */
@DisplayName("TransactionController Unit Tests")
@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    @Test
    @DisplayName("performTransaction should return 200 OK with approved code when transaction is authorized")
    void shouldReturn200WithApprovedCodeWhenTransactionAuthorized() {
        TransactionModel model = new TransactionModel("123", new BigDecimal("100.00"), "5811", "PADARIA DO ZE");
        when(service.performTransaction(any())).thenReturn(new TransactionCodeModel(TransactionStatusCode.APPROVED.getCode()));

        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("00", response.getBody().code());
        verify(service).performTransaction(model);
    }

    @Test
    @DisplayName("performTransaction should return 200 OK with insufficient funds code when balance is too low")
    void shouldReturn200WithInsufficientFundsCodeWhenBalanceLow() {
        TransactionModel model = new TransactionModel("123", new BigDecimal("500.00"), "5411", "SUPERMERCADO");
        when(service.performTransaction(any())).thenReturn(new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode()));

        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("51", response.getBody().code());
    }

    @Test
    @DisplayName("performTransaction should return 200 OK with processing error code when service fails")
    void shouldReturn200WithProcessingErrorCodeWhenServiceFails() {
        TransactionModel model = new TransactionModel("999", new BigDecimal("100.00"), "0000", "MERCHANT");
        when(service.performTransaction(any())).thenReturn(new TransactionCodeModel(TransactionStatusCode.PROCESSING_ERROR.getCode()));

        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(model);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("07", response.getBody().code());
    }
}
