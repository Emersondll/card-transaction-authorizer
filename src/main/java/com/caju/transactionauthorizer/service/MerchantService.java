package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantDocument;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service interface for managing merchants.
 */
@Service
public interface MerchantService {
    /**
     * Finds a merchant by name.
     *
     * @param merchant the name of the merchant to find
     * @return an Optional containing the MerchantDocument if found, or an 
empty Optional otherwise
     */
    Optional<MerchantDocument> findByName(String merchant);
}