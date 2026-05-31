package com.caju.transactionauthorizer.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;
import com.caju.transactionauthorizer.service.MerchantService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for merchant look-up operations.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Querying the merchant override table by name to support the L3 rule.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see MerchantRepository for persistence operations
 */
@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository for merchant persistence operations (non-null)
     * @throws NullPointerException if {@code repository} is null
     */
    public MerchantServiceImpl(MerchantRepository repository) {
        this.repository = Objects.requireNonNull(repository, "MerchantRepository cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<MerchantDocument> findByName(String merchant) {
        Objects.requireNonNull(merchant, "Merchant name cannot be null");
        log.debug("Looking up merchant override. merchant={}", merchant);
        return repository.findByName(merchant);
    }
}
