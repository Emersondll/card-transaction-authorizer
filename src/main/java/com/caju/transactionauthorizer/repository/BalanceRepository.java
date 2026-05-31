package com.caju.transactionauthorizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.caju.transactionauthorizer.document.BalanceDocument;

/**
 * MongoDB repository for {@link BalanceDocument} persistence operations.
 *
 * <p>Spring Data MongoDB automatically generates the implementation at runtime.
 * Optimistic locking is enforced by the {@code @Version} field on
 * {@link BalanceDocument}, preventing concurrent updates from corrupting balances.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface BalanceRepository extends MongoRepository<BalanceDocument, String> {

    /**
     * Finds the balance document associated with the given account identifier.
     *
     * @param account the account identifier to search for (non-null)
     * @return an {@link Optional} containing the balance if found, empty otherwise
     */
    Optional<BalanceDocument> findByAccount(String account);
}
