package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.BalanceMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BalanceRepositoryAdapterTest {

    @Mock BalanceMongoRepository repository;
    @InjectMocks BalanceRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByAccount_found_returnsMappedBalance() {
        BalanceDocument doc = new BalanceDocument("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(repository.findByAccount("acc")).thenReturn(Optional.of(doc));

        Optional<Balance> result = adapter.findByAccount("acc");

        assertTrue(result.isPresent());
        assertEquals("acc", result.get().getAccount());
        assertEquals(new BigDecimal("100.00"), result.get().getFood());
    }

    @Test
    void findByAccount_notFound_returnsEmpty() {
        when(repository.findByAccount("acc")).thenReturn(Optional.empty());
        assertTrue(adapter.findByAccount("acc").isEmpty());
    }

    @Test
    void save_persistsBalance() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        adapter.save(balance);
        verify(repository).save(any(BalanceDocument.class));
    }
}
