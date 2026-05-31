package com.caju.transactionauthorizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.caju.transactionauthorizer.document.MerchantDocument;

/**
 * MongoDB repository for {@link MerchantDocument} persistence operations.
 *
 * <p>Used to implement the L3 merchant-override rule: looks up a merchant by name
 * and, if found, uses the stored MCC instead of the one in the transaction payload.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface MerchantRepository extends MongoRepository<MerchantDocument, String> {

    /**
     * Finds a merchant by its exact name as it appears in transaction payloads.
     *
     * @param merchant the merchant name to search for (non-null)
     * @return an {@link Optional} containing the merchant if found, empty otherwise
     */
    Optional<MerchantDocument> findByName(String merchant);
}
