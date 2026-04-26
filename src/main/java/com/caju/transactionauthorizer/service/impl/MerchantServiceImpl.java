package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;
import com.caju.transactionauthorizer.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class implements the MerchantService interface and is responsible f
f
for managing merchant data.
 */
@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantRepository repository;

    /**
     * Finds a merchant by its name using repository.findByName().
     * This method is part of the MerchantService interface and is called w
w
with a given merchant name.
     */
    @Override
    public Optional<MerchantDocument> findByName(final String merchant) {
        return retrieveMerchantData(merchant);
    }

    /**
     * Helper method for retrieving merchant data from the database. It's c
c
called from findByName() and uses repository.findByName().
     */
    private Optional<MerchantDocument> retrieveMerchantData(String name) {
        return repository.findByName(name);
    }
}