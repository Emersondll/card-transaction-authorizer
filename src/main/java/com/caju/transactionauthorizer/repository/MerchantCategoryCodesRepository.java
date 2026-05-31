package com.caju.transactionauthorizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;

/**
 * MongoDB repository for {@link MerchantCategoryCodesDocument} persistence operations.
 *
 * <p>Stores the MCC-to-category mapping table used to resolve which balance bucket
 * should be debited for each transaction (L1 and L2 rules).</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface MerchantCategoryCodesRepository extends MongoRepository<MerchantCategoryCodesDocument, String> {

    /**
     * Finds the category mapping for a given MCC code.
     *
     * @param mcc the 4-digit Merchant Category Code to look up (non-null)
     * @return an {@link Optional} containing the mapping if found, empty otherwise
     */
    Optional<MerchantCategoryCodesDocument> findByCode(String mcc);
}
