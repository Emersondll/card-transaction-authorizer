package com.caju.transactionauthorizer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.caju.transactionauthorizer.document.TransactionDocument;

/**
 * MongoDB repository for {@link TransactionDocument} persistence operations.
 *
 * <p>Used to persist an audit record of every successfully authorized transaction.
 * Spring Data MongoDB automatically generates the implementation at runtime.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Repository
public interface TransactionRepository extends MongoRepository<TransactionDocument, String> {
}
