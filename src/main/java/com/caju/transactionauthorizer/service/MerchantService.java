package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;

import java.util.Optional;

public interface MerchantService {
    Optional<MerchantDocument> findByName(final String merchant);
}