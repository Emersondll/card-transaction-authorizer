package com.caju.transactionauthorizer.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing a persisted financial transaction.
 *
 * <p>Created after a transaction is successfully authorized. Acts as an immutable
 * audit record of every debit/credit operation processed by the authorizer.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.repository.TransactionRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transaction")
public class TransactionDocument {

    /**
     * Unique transaction identifier — a UUID generated at authorization time.
     * Acts as the MongoDB {@code _id} field.
     */
    @Id
    private String id;

    /**
     * Reference to the account that originated this transaction.
     */
    private String accountId;

    /**
     * Monetary value debited from the account balance.
     */
    private BigDecimal amount;

    /**
     * Name of the merchant where the transaction occurred.
     */
    private String merchant;

    /**
     * Merchant Category Code (MCC) used to determine the balance bucket.
     */
    private String mcc;

    /**
     * UTC timestamp recording when the transaction was authorized and persisted.
     */
    private LocalDateTime timestamp;
}
