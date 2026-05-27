package com.caju.transactionauthorizer.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "transaction")
public record TransactionDocument(
        @Id String id,
        String accountId,
        BigDecimal amount,
        String merchant,
        String mcc,
        Instant timestamp
) {}
