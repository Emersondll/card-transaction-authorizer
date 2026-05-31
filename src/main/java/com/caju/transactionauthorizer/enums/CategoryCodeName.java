package com.caju.transactionauthorizer.enums;

/**
 * Enumeration of the three benefit wallet categories supported by the authorizer.
 *
 * <p>The MCC of each transaction is mapped to one of these categories to determine
 * which balance bucket will be debited:
 * <ul>
 *   <li>{@link #FOOD} — grocery stores (MCC {@code 5411}, {@code 5412})</li>
 *   <li>{@link #MEAL} — restaurants and fast food (MCC {@code 5811}, {@code 5812})</li>
 *   <li>{@link #CASH} — all other merchants and the universal fallback bucket</li>
 * </ul>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public enum CategoryCodeName {

    /** Grocery and supermarket purchases — MCCs 5411 and 5412. */
    FOOD("Food"),

    /** Restaurant and fast-food purchases — MCCs 5811 and 5812. */
    MEAL("Meal"),

    /** General cash category — used for all other MCCs and as fallback. */
    CASH("Cash");

    private final String displayValue;

    /**
     * Constructs the enum constant with a human-readable display value.
     *
     * @param displayValue the display label for this category
     */
    CategoryCodeName(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Returns the human-readable display label for this category.
     *
     * @return display value (e.g., {@code "Food"}, {@code "Meal"}, {@code "Cash"})
     */
    public String getDisplayValue() {
        return displayValue;
    }
}
