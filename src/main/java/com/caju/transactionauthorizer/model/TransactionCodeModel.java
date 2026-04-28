package com.caju.transactionauthorizer.model;

/**
 * This class represents a model for transaction codes, with a single attri
attribute (transaction code). The class utilizes Java record feature to enh
enhance readability and immutability. The getTransactionCode() method simpl
simply returns the value of the 'code' field in this record.
 */
public final record TransactionCodeModel(String code) {
    /**
     * Returns the transaction code.
     *
     * This method directly accesses the 'code' attribute, providing an eas
easier way to retrieve the transaction code without exposing the underlying
underlying record structure.
     *
     * @return the transaction code
     */
    public String getTransactionCode() {
        return code;
    }
}