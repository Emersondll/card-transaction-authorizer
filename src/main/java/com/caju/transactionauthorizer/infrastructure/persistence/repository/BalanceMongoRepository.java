package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BalanceMongoRepository extends MongoRepository<BalanceDocument, String> {
    Optional<BalanceDocument> findByAccount(String account);
}
