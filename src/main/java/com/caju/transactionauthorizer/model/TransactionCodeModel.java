package com.caju.transactionauthorizer.model;

/**
 * This class represents a model for transaction codes, with a single attri
attribute - the unique transaction code. The class uses the new Java record
record feature to provide better readability and immutability.
 */
public final record TransactionCodeModel(String code) {
}