package com.caju.transactionauthorizer.infrastructure.persistence.document;

import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mcc")
public record MerchantCategoryDocument(@Id String id, String code, CategoryCode description) {}
