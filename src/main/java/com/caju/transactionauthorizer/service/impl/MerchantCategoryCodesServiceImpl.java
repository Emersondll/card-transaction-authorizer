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

    private final MerchantCategoryCodesRepository repository;

    @Autowired
    public MerchantCategoryCodesServiceImpl(MerchantCategoryCodesRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MerchantCategoryCodesDocument> findByCode(final String mcc) {
        return repository.findByCode(mcc);
    }

    @Override
    public CategoryCodeName checkCategory(final String mcc) {

        final Optional<MerchantCategoryCodesDocument> document = findByCode(mcc);
        if (document.isEmpty() || Objects.isNull(mcc)) {
            return CategoryCodeName.CASH;
        } else {
            return validateCategory(mcc, document.get());
        }

    }

    private CategoryCodeName validateCategory(final String mcc, final MerchantCategoryCodesDocument codesDocument) {
        if (mcc.equals(codesDocument.getCode())) {
            return codesDocument.getDescription();
        } else {
            return CategoryCodeName.CASH;
        }
    }

}