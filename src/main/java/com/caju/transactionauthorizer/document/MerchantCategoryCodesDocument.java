package com.caju.transactionauthorizer.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.caju.transactionauthorizer.enums.CategoryCodeName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MongoDB document entity representing the mapping between an MCC code and a benefit category.
 *
 * <p>Defines the authoritative MCC-to-category rules used by the authorizer:
 * <ul>
 *   <li>{@code 5411}, {@code 5412} → {@link CategoryCodeName#FOOD}</li>
 *   <li>{@code 5811}, {@code 5812} → {@link CategoryCodeName#MEAL}</li>
 *   <li>All others → {@link CategoryCodeName#CASH} (default fallback)</li>
 * </ul>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mcc")
public class MerchantCategoryCodesDocument {

    /**
     * Unique document identifier (MongoDB {@code _id}).
     */
    @Id
    private String id;

    /**
     * The 4-digit MCC code (e.g., {@code "5411"}, {@code "5811"}).
     */
    private String code;

    /**
     * The benefit category associated with this MCC code.
     * One of {@code FOOD}, {@code MEAL}, or {@code CASH}.
     */
    private CategoryCodeName description;
}
