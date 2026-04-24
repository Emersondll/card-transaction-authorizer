package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.BalanceDocument;

import java.util.Optional;

public interface BalanceService {
    Optional<BalanceDocument> findByAccount(final String account);

    void save(BalanceDocument balanceDocument);
}