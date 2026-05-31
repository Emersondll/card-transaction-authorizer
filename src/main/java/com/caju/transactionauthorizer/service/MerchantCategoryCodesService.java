package com.caju.transactionauthorizer.service;

import java.util.Optional;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;

/**
 * Service contract for MCC-to-category resolution operations.
 *
 * <p>Implements the L1 and L2 MCC mapping rules:
 * <ul>
 *   <li>MCC {@code 5411} or {@code 5412} → {@link CategoryCodeName#FOOD}</li>
 *   <li>MCC {@code 5811} or {@code 5812} → {@link CategoryCodeName#MEAL}</li>
 *   <li>Any unrecognised or null MCC → {@link CategoryCodeName#CASH} (default)</li>
 * </ul>
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.service.impl.MerchantCategoryCodesServiceImpl
 */
public interface MerchantCategoryCodesService {

    /**
     * Retrieves the MCC mapping document for the given code.
     *
     * @param mcc the 4-digit MCC code to look up (non-null)
     * @return an {@link Optional} containing the mapping if found, empty otherwise
     */
    Optional<MerchantCategoryCodesDocument> findByCode(String mcc);

    /**
     * Resolves the benefit category for the given MCC code.
     *
     * <p>Returns {@link CategoryCodeName#CASH} if the MCC is {@code null},
     * blank, or not found in the mapping table.</p>
     *
     * @param mcc the 4-digit MCC code to resolve (may be null)
     * @return the resolved {@link CategoryCodeName}; never {@code null}
     */
    CategoryCodeName checkCategory(String mcc);
}
