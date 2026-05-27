package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.port.out.TransactionRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.document.TransactionDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.TransactionMongoRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionMongoRepository repository;

    public TransactionRepositoryAdapter(TransactionMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String accountId, BigDecimal amount, String merchant, String mcc) {
        repository.save(new TransactionDocument(
                UUID.randomUUID().toString(), accountId, amount, merchant, mcc, Instant.now()));
    }
}
