package com.caju.transactionauthorizer.enums;

public enum TransactionStatusCode  {
    APPROVED("00"),
    INSUFFICIENT_FUNDS("51"),
    PROCESSING_ERROR("07");

    private final String code;

    TransactionStatusCode(final String code)  {
        this.code = code;
    }

    public String getCode()  {
        return code;
    }
}