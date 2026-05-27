package com.caju.transactionauthorizer.domain.model;

public enum AuthorizationStatus {
    APPROVED("00"),
    INSUFFICIENT_FUNDS("51"),
    PROCESSING_ERROR("07");

    private final String code;

    AuthorizationStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
