package com.caju.transactionauthorizer.service.impl;

import java.math.BigDecimal;

/**
 * Shared constants used across unit tests to ensure consistency and avoid magic values.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public final class TestConstants {

    /** Sample account identifier used in tests. */
    public static final String ACCOUNT_ID = "accountId";

    /** Sample merchant name used in tests. */
    public static final String MERCHANT = "PADARIA DO ZE  SAO PAULO BR";

    /** Sample MCC code — not mapped to FOOD or MEAL, resolves to CASH. */
    public static final String MCC = "5510";

    /** Generic document/entity identifier used in tests. */
    public static final String ID = "id";

    /** Sample account field value used in balance documents. */
    public static final String ACCOUNT = "account";

    /** Amount of R$ 100.00 — sufficient to trigger various balance scenarios. */
    public static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");

    /** Amount of R$ 50.00 — used in partial-balance scenarios. */
    public static final BigDecimal AMOUNT_50 = new BigDecimal("50.00");

    /** Amount of R$ 200.00 — sufficient balance for most test scenarios. */
    public static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    /** Generic error message for exception-related tests. */
    public static final String UNEXPECTED_ERROR_MSG = "Unexpected error";

    /** Zero amount — used to represent an empty balance bucket. */
    public static final BigDecimal AMOUNT_0 = BigDecimal.ZERO;

    /** Optimistic-lock version value used in balance document construction. */
    public static final Long VERSION_1L = 1L;

    private TestConstants() {
    }
}
