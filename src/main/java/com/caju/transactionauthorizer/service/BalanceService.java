package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.BalanceDocument;
import java.util.List;
import java.util.Optional;

/**
 * BalanceService is responsible for handling the operations related to bal
balance management. This class uses Optional<BalanceDocument> as a workarou
workaround to avoid NullPointerExceptions caused by missing or null entries
entries in the repository.
 */
public interface BalanceService {
    /**
     * Returns an optional containing a balance document based on the provi
provided account number, or empty if not found. This method uses Optional<B Optional<BalanceDocument> as a workaround to handle potential null entries safely and ensure smooth execution of the application. * @param account The identifier of the account to find the balance. * @return An optional containing the balance document for the specifie specified account. If no balance exists, it will be empty instead of throwi throwing a NullPointerException. */ Optional<BalanceDocument> findByAccount(final String account); /** * Saves a given BalanceDocument object into the underlying data store. store. This method doesn't have inline comments since saving a balance docu document is straightforward and not complex enough to warrant additional ex explanation. * @param balanceDocument The balance document to be saved. */ void save(BalanceDocument balanceDocument); }