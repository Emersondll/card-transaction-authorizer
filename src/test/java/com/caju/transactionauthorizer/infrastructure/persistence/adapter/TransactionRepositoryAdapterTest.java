package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.infrastructure.persistence.document.TransactionDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.TransactionMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class TransactionRepositoryAdapterTest {

    @Mock TransactionMongoRepository repository;
    @InjectMocks TransactionRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void save_persistsTransactionDocument() {
        adapter.save("123", new BigDecimal("50.00"), "PADARIA", "5411");
        verify(repository).save(any(TransactionDocument.class));
    }
}
