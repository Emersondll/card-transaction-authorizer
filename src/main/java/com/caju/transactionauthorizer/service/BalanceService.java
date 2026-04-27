package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.BalanceDocument;
import java.util.Optional;

/**
 * BalanceService is responsible for handling the operations related to bal
balances management.
 */
public interface BalanceService {
    /**
     * Returns an optional containing a balance document based on the provi
provided account number, or empty if not found.
     * @param account The identifier of the account to find the balance.
     * @return An optional containing the balance document for the specifie
specified account.
     */
    Optional<BalanceDocument> findByAccount(final String account);

    /**
     * Saves a given BalanceDocument object into the underlying data store.
store.
     * @param balanceDocument The balance document to be saved.
     */
    void save(BalanceDocument balanceDocument);
}