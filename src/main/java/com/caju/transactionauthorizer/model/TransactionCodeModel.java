package com.caju.transactionauthorizer.model;

import java.util.List;
import java.util.Optional;

public final record TransactionCodeModel(String code) {
    /**
     * Returns the transaction code.
     *
     * This method directly accesses the 'code' attribute, providing an eas
eas
eas
eas
easier way to retrieve the transaction code without exposing the underly
underlying
underlying record structure.
     *
     * @return the transaction code
     */
    public String getTransactionCode() {
        return code;
    }
}