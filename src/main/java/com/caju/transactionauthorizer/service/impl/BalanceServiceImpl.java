package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing balance operations.
 */
@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository repository;
    
    /**
     * Constructs a new instance of BalanceServiceImpl with the specified repository.
     *
     * @param repository the repository to interact with balance data
     */
    @Autowired
    public BalanceServiceImpl(final BalanceRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Finds a balance document by account ID.
     *
     * @param accountId the unique identifier of the account
     * @return an Optional containing the balance document if found, otherwise an empty Optional
     */
    @Override
    public Optional<BalanceDocument> findByAccount(final String accountId) {
        return repository.findByAccount(accountId);
    }

    /**
     * Saves a balance document.
     *
     * @param balance the balance document to save
     */
    @Override
    public void save(final BalanceDocument balance) {
        repository.save(balance);
    }
}