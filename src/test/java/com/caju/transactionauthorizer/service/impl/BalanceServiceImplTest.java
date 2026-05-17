package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    @Mock
    private BalanceRepository repository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private BalanceDocument sampleDocument;
    private final String TEST_ACCOUNT_ID = "ACC123";

    @BeforeEach
    void setUp() {
        // Initialize a sample document for testing
        sampleDocument = new BalanceDocument();
        sampleDocument.setId("DOC1");
        sampleDocument.setAccount(TEST_ACCOUNT_ID);
        sampleDocument.setFood(new BigDecimal("10.00"));
        sampleDocument.setMeal(new BigDecimal("20.00"));
        sampleDocument.setCash(new BigDecimal("5.00"));
    }

    @Test
    void findByAccount_shouldReturnOptionalOfDocumentWhenFound() {
        // Arrange
        when(repository.findByAccount(TEST_ACCOUNT_ID)).thenReturn(Optional.of(sampleDocument));

        // Act
        final Optional<BalanceDocument> result = balanceService.findByAccount(TEST_ACCOUNT_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(sampleDocument, result.get());
        
        // Verify interaction
        verify(repository).findByAccount(TEST_ACCOUNT_ID);
    }

    @Test
    void findByAccount_shouldReturnEmptyOptionalWhenNotFound() {
        // Arrange
        when(repository.findByAccount(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());

        // Act
        final Optional<BalanceDocument> result = balanceService.findByAccount(TEST_ACCOUNT_ID);

        // Assert
        assertTrue(result.isEmpty());
        
        // Verify interaction
        verify(repository).findByAccount(TEST_ACCOUNT_ID);
    }

    @Test
    void findByAccount_shouldHandleDifferentAccountId() {
        // Arrange
        final String otherAccountId = "ACC456";
        when(repository.findByAccount(otherAccountId)).thenReturn(Optional.of(sampleDocument));

        // Act
        final Optional<BalanceDocument> result = balanceService.findByAccount(otherAccountId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(sampleDocument, result.get());
        
        // Verify interaction
        verify(repository).findByAccount(otherAccountId);
    }

    @Test
    void save_shouldCallRepositorySaveWithProvidedDocument() {
        // Arrange
        final BalanceDocument documentToSave = new BalanceDocument();
        documentToSave.setId("NEW_DOC");
        documentToSave.setAccount("ACC_NEW");

        // Act
        balanceService.save(documentToSave);

        // Assert
        // Verify that save was called exactly once with the correct object
        verify(repository, times(1)).save(documentToSave);
    }

    @Test
    void save_shouldHandleNullDocumentGracefully() {
        // Although the method signature doesn't prevent null, testing the repository interaction
        // when a null object is passed is good practice for robustness.
        // We assume the underlying repository handles nulls, but we verify the call.
        
        // Act
        balanceService.save(null);

        // Assert
        // Verify that save was called exactly once with null
        verify(repository, times(1)).save(null);
    }
}