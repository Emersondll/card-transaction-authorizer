package com.caju.transactionauthorizer.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BalanceServiceImpl}.
 */
@DisplayName("BalanceServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository repository;

    private BalanceService service;

    @BeforeEach
    void setUp() {
        service = new BalanceServiceImpl(repository);
    }

    @Test
    @DisplayName("findByAccount should return balance when account exists")
    void shouldReturnBalanceWhenAccountExists() {
        BalanceDocument balance = new BalanceDocument("id", "acc1", BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, 1L);
        when(repository.findByAccount("acc1")).thenReturn(Optional.of(balance));

        Optional<BalanceDocument> result = service.findByAccount("acc1");

        assertTrue(result.isPresent());
        assertEquals("acc1", result.get().getAccount());
    }

    @Test
    @DisplayName("findByAccount should return empty when account does not exist")
    void shouldReturnEmptyWhenAccountNotFound() {
        when(repository.findByAccount("unknown")).thenReturn(Optional.empty());

        Optional<BalanceDocument> result = service.findByAccount("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("save should delegate to repository")
    void shouldDelegateToRepository() {
        BalanceDocument balance = new BalanceDocument("id", "acc1", BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, 1L);

        service.save(balance);

        verify(repository).save(balance);
    }
}
