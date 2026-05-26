package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MerchantCategoryCodesServiceImpl implements MerchantCategoryCodesService {

    /**
     * Finds a merchant category codes document by its code.
     *
     * @param mcc the unique identifier of the merchant category code
     * @return an Optional containing the merchant category codes document if found, otherwise an empty Optional
     */
    @Autowired
    private MerchantCategoryCodesRepository repository;

    @Override
    public Optional<MerchantCategoryCodesDocument> findByCode(final String mcc) {
        return repository.findByCode(mcc);
    }

    /**
     * Checks the category of a merchant based on its code.
     *
     * @param mcc the unique identifier of the merchant category code
     * @return the category code name
     */
    @Override
    public CategoryCodeName checkCategory(final String mcc) {
        final Optional<MerchantCategoryCodesDocument> document = findByCode(mcc);
        if (document.isEmpty() || Objects.isNull(mcc)) {
            return CategoryCodeName.CASH;
        } else {
            return validateCategory(mcc, document.get());
        }
    }

    /**
     * Validates the category of a merchant based on its code and document.
     *
     * @param mcc the unique identifier of the merchant category code
     * @param codesDocument the merchant category codes document
     * @return the category code name
     */
    private CategoryCodeName validateCategory(final String mcc, final MerchantCategoryCodesDocument codesDocument) {
        if (mcc.equals(codesDocument.getCode())) {
            return codesDocument.getDescription();
        } else {
            return CategoryCodeName.CASH;
        }
    }

}