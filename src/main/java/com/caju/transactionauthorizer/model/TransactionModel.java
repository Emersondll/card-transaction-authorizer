package com.caju.transactionauthorizer.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Immutable record representing an incoming transaction authorization request.
 *
 * <p>Deserialized from the JSON payload posted to {@code POST /transaction}.
 * All fields are validated via Bean Validation before the handler is invoked.</p>
 *
 * <p>Example payload:
 * <pre>{@code
 * {
 *   "account": "123",
 *   "totalAmount": 100.00,
 *   "mcc": "5811",
 *   "merchant": "PADARIA DO ZE  SAO PAULO BR"
 * }
 * }</pre>
 *
 * @param account     the account identifier to debit (non-null, non-blank)
 * @param totalAmount the monetary amount to debit; must be positive (non-null)
 * @param mcc         the 4-digit Merchant Category Code used to select the balance bucket
 * @param merchant    the merchant name; if found in the merchant override table,
 *                    the stored MCC takes precedence over the {@code mcc} field (L3 rule)
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record TransactionModel(

        @JsonProperty("account")
        @NotBlank(message = "Account is required")
        String account,

        @JsonProperty("totalAmount")
        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmount,

        @JsonProperty("mcc")
        @NotBlank(message = "MCC is required")
        String mcc,

        @JsonProperty("merchant")
        @NotBlank(message = "Merchant is required")
        String merchant

) {
    /**
     * Compact constructor — allows null values so that Bean Validation annotations
     * ({@code @NotBlank}, {@code @NotNull}, {@code @Positive}) can provide detailed
     * field-level error messages via {@code @Valid} on the controller.
     *
     * <p>If the constructor threw on null, Spring MVC would propagate an
     * {@code HttpMessageNotReadableException} (500) before Bean Validation had a chance
     * to produce structured 400 responses. The {@code @NotNull}/{@code @NotBlank}
     * annotations on the fields handle null/blank detection at the HTTP boundary.</p>
     */
    public TransactionModel {
        // Null validation is handled by @NotNull/@NotBlank Bean Validation annotations.
        // The compact constructor intentionally accepts nulls so the validation layer
        // can produce field-level 400 responses instead of a 500 from a NullPointerException.
    }
}
