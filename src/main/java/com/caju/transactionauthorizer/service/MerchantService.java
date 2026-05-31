package com.caju.transactionauthorizer.service;

import java.util.Optional;

import com.caju.transactionauthorizer.document.MerchantDocument;

/**
 * Service contract for merchant look-up operations.
 *
 * <p>Used to implement the L3 merchant-precedence rule: if the transaction's merchant name
 * matches a record in the merchant override table, the stored MCC replaces the one in the
 * transaction payload before category resolution.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.service.impl.MerchantServiceImpl
 */
public interface MerchantService {

    /**
     * Looks up a merchant by its exact name as it appears in the transaction payload.
     *
     * @param merchant the merchant name to search for (non-null)
     * @return an {@link Optional} containing the merchant document if an override exists,
     *         empty otherwise
     */
    Optional<MerchantDocument> findByName(String merchant);
}
