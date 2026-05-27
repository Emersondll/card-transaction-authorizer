package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.TransactionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMongoRepository extends MongoRepository<TransactionDocument, String> {}
