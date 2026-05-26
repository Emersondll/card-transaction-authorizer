package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    @Mock
    private BalanceRepository repository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private BalanceDocument balanceDocument;

    @BeforeEach
    public void setUp() {
        balanceDocument = new BalanceDocument("1", "account123", BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(200), 1L);
    }

    @Test
    public void testFindByAccount_ExistingAccount() {
        when(repository.findByAccount(anyString())).thenReturn(Optional.of(balanceDocument));

        final Optional<BalanceDocument> result = balanceService.findByAccount("account123");

        assertTrue(result.isPresent());
        assertEquals(balanceDocument, result.get());
    }

    @Test
    public void testFindByAccount_NonExistingAccount() {
        when(repository.findByAccount(anyString())).thenReturn(Optional.empty());

        final Optional<BalanceDocument> result = balanceService.findByAccount("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    public void testSave_ValidBalanceDocument() {
        balanceService.save(balanceDocument);

        Mockito.verify(repository, Mockito.times(1)).save(balanceDocument);
    }
}