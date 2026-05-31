package com.caju.transactionauthorizer.document;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing the wallet balance for a given account.
 *
 * <p>Holds three independent balance buckets — {@code food}, {@code meal}, and {@code cash} —
 * that are debited according to the MCC of each transaction.</p>
 *
 * <p>Optimistic Locking: The {@code @Version} field prevents concurrent updates from
 * overwriting each other. If two transactions try to debit the same account simultaneously,
 * Spring Data MongoDB will throw an {@link org.springframework.dao.OptimisticLockingFailureException}
 * for the second one, which is caught by the service layer and returned as a processing error
 * (code {@code "07"}). This addresses the L4 concurrent-transaction challenge.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.repository.BalanceRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "balance")
public class BalanceDocument {

    /**
     * Unique document identifier (MongoDB {@code _id}).
     */
    @Id
    private String id;

    /**
     * The account identifier this balance belongs to.
     * Indexed for fast look-up by account.
     */
    private String account;

    /**
     * Available food benefit balance (FOOD bucket).
     * Debited when MCC is {@code 5411} or {@code 5412}.
     */
    private BigDecimal food;

    /**
     * Available meal benefit balance (MEAL bucket).
     * Debited when MCC is {@code 5811} or {@code 5812}.
     */
    private BigDecimal meal;

    /**
     * Available cash balance (CASH bucket).
     * Used for any MCC not mapped to FOOD or MEAL, and as fallback
     * when the primary bucket has insufficient funds.
     */
    private BigDecimal cash;

    /**
     * Optimistic-lock version managed automatically by Spring Data MongoDB.
     * Incremented on every save; prevents lost-update anomalies under concurrency.
     */
    @Version
    private Long version;
}
