package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.port.out.BalanceRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.BalanceMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class BalanceRepositoryAdapter implements BalanceRepositoryPort {

    private final BalanceMongoRepository repository;

    public BalanceRepositoryAdapter(BalanceMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Balance> findByAccount(String accountId) {
        return repository.findByAccount(accountId).map(this::toBalance);
    }

    @Override
    public void save(Balance balance) {
        repository.save(toDocument(balance));
    }

    private Balance toBalance(BalanceDocument doc) {
        return new Balance(doc.getId(), doc.getAccount(), doc.getFood(), doc.getMeal(), doc.getCash(), doc.getVersion());
    }

    private BalanceDocument toDocument(Balance balance) {
        return new BalanceDocument(balance.getId(), balance.getAccount(), balance.getFood(), balance.getMeal(), balance.getCash(), balance.getVersion());
    }
}
