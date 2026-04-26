package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.BalanceDocument;
import java.util.List;
import java.util.Optional;

/**
 * Interface responsible for managing balances from account numbers.
 */
public interface BalanceService {
    // TODO: Consider using caching to improve performance.
    /**
     * Retrieve the balance document associated with the specified account 


number.
     * @param account Number of the account.
     * @return The balance document, if found; otherwise empty Optional.
     */
    Optional<BalanceDocument> findByAccount(final String account);

    /**
     * Store a new or updated balance document in the database.
     * @param balanceDocument The balance document to save.
     */
    void save(BalanceDocument balanceDocument);
}