package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing balance operations.
 */
@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository repository;

    public BalanceServiceImpl(final BalanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BalanceDocument> findByAccount(final String accountId) 
{
        // Use repository to fetch balance document by account ID
        return repository.findByAccount(accountId);
    }

    @Override
    public void save(final BalanceDocument balance) {
        // Save the balance document using the repository
        repository.save(balance);
    }
}