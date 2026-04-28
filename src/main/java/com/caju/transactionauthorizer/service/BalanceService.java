package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.BalanceDocument;
import java.util.List;
import java.util.Optional;

/**
 * BalanceService is responsible for handling the operations related to bal
bal
balance management. This class uses Optional<BalanceDocument> as a worka
workaround to avoid NullPointerExceptions caused by missing or null entries
entries
entries in the repository.
 */
public interface BalanceService {
    /**
     * Returns an optional containing a balance document based on the provi
provi
provided account number, or empty if not found. This method uses Optio
Optional<B Optional<BalanceDocument> as a workaround to handle potential nu null entries safely and ensure smooth execution of the application. */ Optional<BalanceDocument> findByAccount(final String account); /** * Saves a given BalanceDocument object into the underlying data store. store. store. This method doesn't have inline comments since saving a balan balance document is straightforward and not complex enough to warrant addit additional explanation. */ void save(BalanceDocument balanceDocument); }