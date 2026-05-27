package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.Balance;
import java.util.Optional;

public interface BalanceRepositoryPort {
    Optional<Balance> findByAccount(String accountId);
    void save(Balance balance);
}
