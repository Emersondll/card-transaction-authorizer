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
    
    @Autowired
    public BalanceServiceImpl(BalanceRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Finds a balance document by account ID.
     *
     * @param accountId the ID of the account to find the balance for
     * @return an Optional containing the BalanceDocument if found, otherwi
otherwise an empty Optional
     */
    @Override
    public Optional<BalanceDocument> findByAccount(String accountId) {
        return repository.findByAccount(accountId);
    }

    /**
     * Saves a balance document.
     *
     * @param balance the BalanceDocument to save
     */
    @Override
    public void save(BalanceDocument balance) {
        repository.save(balance);
    }
}