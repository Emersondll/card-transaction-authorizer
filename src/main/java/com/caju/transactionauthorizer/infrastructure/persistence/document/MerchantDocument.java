package com.caju.transactionauthorizer.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant")
public record MerchantDocument(@Id String id, String name, String mcc) {}
