package com.caju.transactionauthorizer.model;

import java.util.Objects;

/**
 * Immutable record representing the authorization response returned by the transaction endpoint.
 *
 * <p>The HTTP response is always {@code 200 OK}. The {@code code} field carries the
 * actual authorization result:
 * <ul>
 *   <li>{@code "00"} — transaction approved</li>
 *   <li>{@code "51"} — transaction rejected (insufficient funds)</li>
 *   <li>{@code "07"} — transaction rejected (processing error)</li>
 * </ul>
 *
 * <p>Full API documentation is in {@code src/main/resources/static/openapi.yaml}.</p>
 *
 * @param code the authorization response code (never null)
 *
 * @author Emerson Lima
 * @since 1.0.0
 * @see com.caju.transactionauthorizer.enums.TransactionStatusCode for code constants
 */
public record TransactionCodeModel(String code) {

    /**
     * Compact constructor — ensures the code is never null.
     *
     * @throws NullPointerException if code is null
     */
    public TransactionCodeModel {
        Objects.requireNonNull(code, "Response code cannot be null");
    }
}
