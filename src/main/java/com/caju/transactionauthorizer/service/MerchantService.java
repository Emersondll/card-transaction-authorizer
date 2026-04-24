package com.caju.transactionauthorizer.service;

import com.caju.transactionauthorizer.document.MerchantDocument;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MerchantService {
    Optional<MerchantDocument> findByName(String merchant);
}