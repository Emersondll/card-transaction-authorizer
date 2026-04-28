package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;

import java.util.Optional;

/**
 * This service provides methods to interact with Merchant Category Codes.
 */
public interface MerchantCategoryCodesService {
    /**
     * Searches for a Merchant Category Code by its code and returns the ma
ma
ma
matching document, if found. Otherwise, returns empty optional.
     * @param mcc The merchant category code to search
     * @return An Optional containing the MerchantCategoryCodesDocument obj
obj
obj
object or empty if not found
     */
    Optional<MerchantCategoryCodesDocument> findByCode(final String mcc);

    /**
     * Checks whether a given merchant category code belongs to one of the 


defined categories.
     * @param mcc The merchant category code to check
     * @return A CategoryCodeName enum representing the category, or null i
i
i
if not found
     */
    CategoryCodeName checkCategory(String mcc);
}