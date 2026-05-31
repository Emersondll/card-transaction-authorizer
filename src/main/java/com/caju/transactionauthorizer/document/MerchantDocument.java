package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing a known merchant with a corrected MCC override.
 *
 * <p>Used to implement the L3 merchant-precedence rule: when a transaction's merchant name
 * matches a record in this collection, the stored {@code mcc} overrides the MCC sent in
 * the transaction payload, compensating for incorrect MCCs assigned by the network.</p>
 *
 * <p>Example overrides:
 * <ul>
 *   <li>{@code "UBER EATS  SAO PAULO BR"} → MCC {@code "5811"} (MEAL)</li>
 *   <li>{@code "UBER TRIP  SAO PAULO BR"} → MCC {@code "4111"} (CASH)</li>
 * </ul>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.repository.MerchantRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "merchant")
public class MerchantDocument {

    /**
     * Unique document identifier (MongoDB {@code _id}).
     */
    @Id
    private String id;

    /**
     * The exact merchant name as it appears in the transaction payload.
     * Uniquely indexed for O(log n) case-sensitive look-up (Spec 04 — indexing strategy).
     */
    @Indexed(unique = true)
    private String name;

    /**
     * The corrected MCC code for this merchant.
     * Takes precedence over the MCC provided in the transaction payload.
     */
    private String mcc;
}
