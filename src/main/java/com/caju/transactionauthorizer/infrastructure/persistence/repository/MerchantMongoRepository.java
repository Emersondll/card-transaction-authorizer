package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MerchantMongoRepository extends MongoRepository<MerchantDocument, String> {
    Optional<MerchantDocument> findByName(String name);
}
