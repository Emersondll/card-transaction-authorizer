package com.caju.transactionauthorizer.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.repository.BalanceRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for account balance operations.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Retrieving the balance document for a given account.</li>
 *   <li>Persisting updated balances after a successful debit, leveraging
 *       optimistic locking to detect concurrent modifications.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see BalanceRepository for persistence operations
 */
@Service
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository for balance persistence operations (non-null)
     * @throws NullPointerException if {@code repository} is null
     */
    public BalanceServiceImpl(BalanceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "BalanceRepository cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BalanceDocument> findByAccount(String accountId) {
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        log.debug("Finding balance. accountId={}", accountId);
        return repository.findByAccount(accountId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(BalanceDocument balanceDocument) {
        Objects.requireNonNull(balanceDocument, "BalanceDocument cannot be null");
        log.debug("Saving balance. account={}", balanceDocument.getAccount());
        repository.save(balanceDocument);
    }
}
