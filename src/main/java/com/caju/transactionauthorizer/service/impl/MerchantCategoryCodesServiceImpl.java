package com.caju.transactionauthorizer.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for MCC-to-category resolution.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Querying the MCC mapping table by code.</li>
 *   <li>Resolving the benefit category for a given MCC, defaulting to
 *       {@link CategoryCodeName#CASH} for unknown or null MCCs.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see MerchantCategoryCodesRepository for persistence operations
 */
@Service
@Slf4j
public class MerchantCategoryCodesServiceImpl implements MerchantCategoryCodesService {

    private final MerchantCategoryCodesRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository for MCC mapping persistence operations (non-null)
     * @throws NullPointerException if {@code repository} is null
     */
    public MerchantCategoryCodesServiceImpl(MerchantCategoryCodesRepository repository) {
        this.repository = Objects.requireNonNull(repository, "MerchantCategoryCodesRepository cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MerchantCategoryCodesDocument> findByCode(String mcc) {
        Objects.requireNonNull(mcc, "MCC cannot be null");
        return repository.findByCode(mcc);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns {@link CategoryCodeName#CASH} when {@code mcc} is {@code null} or
     * not present in the mapping table.</p>
     */
    @Override
    public CategoryCodeName checkCategory(String mcc) {
        if (mcc == null) {
            log.debug("MCC is null — defaulting to CASH");
            return CategoryCodeName.CASH;
        }

        return findByCode(mcc)
                .filter(doc -> mcc.equals(doc.getCode()))
                .map(MerchantCategoryCodesDocument::getDescription)
                .orElseGet(() -> {
                    log.debug("MCC not mapped — defaulting to CASH. mcc={}", mcc);
                    return CategoryCodeName.CASH;
                });
    }
}
