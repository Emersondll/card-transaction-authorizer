package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;
import com.caju.transactionauthorizer.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository repository;

    /**
     * Finds a merchant by name.
     *
     * @param merchant the name of the merchant to find
     * @return an Optional containing the MerchantDocument if found, otherw
otherwise an empty Optional
     */
    @Override
    public Optional<MerchantDocument> findByName(final String merchant) {
        return retrieveMerchantData(merchant);
    }
    
    // helper method to retrieve merchant data from the database
    private Optional<MerchantDocument> retrieveMerchantData(String name) {
        return repository.findByName(name);
    }
}