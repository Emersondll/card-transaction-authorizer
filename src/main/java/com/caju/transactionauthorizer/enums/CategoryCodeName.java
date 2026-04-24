package com.caju.transactionauthorizer.enums;

public enum CategoryCodeName {
    FOOD("Food"),
    MEAL("Meal"),
    CASH("Cash");
    
    private final String displayValue;
    
    CategoryCodeName(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
}