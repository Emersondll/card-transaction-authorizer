package com.caju.transactionauthorizer.enums;

/**
 * Enumeration of the ISO 8583-inspired response codes returned by the transaction authorizer.
 *
 * <p>Every transaction attempt results in exactly one of these codes:
 * <ul>
 *   <li>{@link #APPROVED} ({@code "00"}) — transaction authorized and balance debited</li>
 *   <li>{@link #INSUFFICIENT_FUNDS} ({@code "51"}) — primary and fallback balances too low</li>
 *   <li>{@link #PROCESSING_ERROR} ({@code "07"}) — any other error (account not found,
 *       concurrent update conflict, unexpected exception)</li>
 * </ul>
 *
 * <p>The HTTP response is always {@code 200 OK} regardless of the code, per the challenge spec.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public enum TransactionStatusCode {

    /** Transaction was approved and the balance has been debited. */
    APPROVED("00"),

    /** Transaction was rejected due to insufficient balance in all applicable buckets. */
    INSUFFICIENT_FUNDS("51"),

    /** Transaction could not be processed due to a system or data error. */
    PROCESSING_ERROR("07");

    private final String code;

    /**
     * Constructs the enum constant with its wire-format code string.
     *
     * @param code the response code returned in the API response body
     */
    TransactionStatusCode(String code) {
        this.code = code;
    }

    /**
     * Returns the wire-format response code for this status.
     *
     * @return the code string (e.g., {@code "00"}, {@code "51"}, {@code "07"})
     */
    public String getCode() {
        return code;
    }
}
