package com.caju.transactionauthorizer.controller;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.caju.transactionauthorizer.controller.TransactionController;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    @Test
    public void testPerformTransaction_withValidInput() {
        // Arrange
        TransactionModel transactionModel = new TransactionModel("account", BigDecimal.ONE, "mcc", "merchant");
        TransactionCodeModel expectedResponse = new TransactionCodeModel("00");
        Mockito.when(service.performTransaction(transactionModel)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(transactionModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("00", response.getBody().code());
    }

    @Test
    public void testPerformTransaction_withNullInput() {
        // Arrange
        TransactionModel transactionModel = null;
        Mockito.when(service.performTransaction(transactionModel)).thenReturn(null);

        // Act
        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(transactionModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPerformTransaction_withServiceReturningNull() {
        // Arrange
        TransactionModel transactionModel = new TransactionModel("account", BigDecimal.ONE, "mcc", "merchant");
        Mockito.when(service.performTransaction(transactionModel)).thenReturn(null);

        // Act
        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(transactionModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testPerformTransaction_withServiceReturningInsufficientFunds() {
        // Arrange
        TransactionModel transactionModel = new TransactionModel("account", BigDecimal.ONE, "mcc", "merchant");
        TransactionCodeModel expectedResponse = new TransactionCodeModel("51");
        Mockito.when(service.performTransaction(transactionModel)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(transactionModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("51", response.getBody().code());
    }

    @Test
    public void testPerformTransaction_withServiceReturningProcessingError() {
        // Arrange
        TransactionModel transactionModel = new TransactionModel("account", BigDecimal.ONE, "mcc", "merchant");
        TransactionCodeModel expectedResponse = new TransactionCodeModel("07");
        Mockito.when(service.performTransaction(transactionModel)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<TransactionCodeModel> response = controller.performTransaction(transactionModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("07", response.getBody().code());
    }
}