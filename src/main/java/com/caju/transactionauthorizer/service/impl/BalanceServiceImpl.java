package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository repository;

    @Autowired
    public BalanceServiceImpl(BalanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BalanceDocument> findByAccount(String accountId) {
        return repository.findByAccount(accountId);
    }

    @Override
    public void save(BalanceDocument balance) {
        repository.save(balance);
    }
}