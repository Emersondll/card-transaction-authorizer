package com.caju.transactionauthorizer.service;

import java.util.Optional;

import com.caju.transactionauthorizer.document.BalanceDocument;

/**
 * Service contract for account balance operations.
 *
 * <p>Provides read and write access to the balance document for a given account.
 * Implementations must ensure thread-safety for concurrent debit operations
 * (enforced via optimistic locking on {@link BalanceDocument#getVersion()}).</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.service.impl.BalanceServiceImpl
 */
public interface BalanceService {

    /**
     * Retrieves the balance document for the given account identifier.
     *
     * @param accountId the account identifier to search for (non-null)
     * @return an {@link Optional} containing the balance if the account exists,
     *         empty if no balance record is found
     */
    Optional<BalanceDocument> findByAccount(String accountId);

    /**
     * Persists (creates or updates) the given balance document.
     *
     * <p>If a concurrent modification has incremented the version since the document
     * was loaded, Spring Data MongoDB will throw an
     * {@link org.springframework.dao.OptimisticLockingFailureException}.</p>
     *
     * @param balanceDocument the balance document to persist (non-null)
     */
    void save(BalanceDocument balanceDocument);
}
