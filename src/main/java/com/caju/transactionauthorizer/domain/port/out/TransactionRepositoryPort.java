package com.caju.transactionauthorizer.domain.port.out;

import java.math.BigDecimal;

public interface TransactionRepositoryPort {
    void save(String accountId, BigDecimal amount, String merchant, String mcc);
}
