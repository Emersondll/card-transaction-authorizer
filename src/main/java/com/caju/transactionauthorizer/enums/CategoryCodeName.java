package com.caju.transactionauthorizer.enums;

public enum CategoryCodeName {
    FOOD("Food"),
    MEAL("Meal"),
    CASH("Cash");
    
    private final String displayValue;
    
    CategoryCodeName(final String displayValue) {
        this.displayValue = displayValue;
    }
    
    
}