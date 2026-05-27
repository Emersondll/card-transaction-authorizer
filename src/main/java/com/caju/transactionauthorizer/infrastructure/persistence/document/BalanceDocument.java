package com.caju.transactionauthorizer.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "balance")
public class BalanceDocument {
    @Id private String id;
    private String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;
    @Version private Long version;
}
