package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;

/**
 * Service contract for transaction authorization operations.
 *
 * <p>The single entry point for the authorization flow, covering all four challenge levels:
 * <ul>
 *   <li><b>L1</b>: Uses MCC to select the balance bucket to debit.</li>
 *   <li><b>L2</b>: Falls back to CASH bucket when primary balance is insufficient.</li>
 *   <li><b>L3</b>: Overrides MCC with merchant-specific mapping when the merchant name matches.</li>
 *   <li><b>L4</b>: Concurrent safety via optimistic locking on {@link com.caju.transactionauthorizer.document.BalanceDocument}.</li>
 * </ul>
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.service.impl.TransactionServiceImpl
 */
public interface TransactionService {

    /**
     * Authorizes and processes a transaction request.
     *
     * <p>Returns a {@link TransactionCodeModel} with code:
     * <ul>
     *   <li>{@code "00"} — approved and balance debited</li>
     *   <li>{@code "51"} — rejected, insufficient balance in all applicable buckets</li>
     *   <li>{@code "07"} — rejected, account not found or unexpected processing error</li>
     * </ul>
     *
     * <p>This method never throws — all errors are captured and mapped to code {@code "07"}.</p>
     *
     * @param transactionModel the incoming transaction request (non-null)
     * @return the authorization result code; never {@code null}
     */
    TransactionCodeModel performTransaction(TransactionModel transactionModel);
}
