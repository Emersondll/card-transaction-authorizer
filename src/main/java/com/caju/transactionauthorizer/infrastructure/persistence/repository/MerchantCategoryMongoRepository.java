package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantCategoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MerchantCategoryMongoRepository extends MongoRepository<MerchantCategoryDocument, String> {
    Optional<MerchantCategoryDocument> findByCode(String code);
}
