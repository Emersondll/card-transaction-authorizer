package com.caju.transactionauthorizer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransactionCodeModelTest {

    @Test
    public void testTransactionCodeModel() {
        // Test with a valid non-null code
        final String validCode = "12345";
        final TransactionCodeModel transactionCodeModel = new TransactionCodeModel(validCode);
        assertEquals(validCode, transactionCodeModel.code());

        // Test with null code
        final TransactionCodeModel nullTransactionCodeModel = new TransactionCodeModel(null);
        assertNull(nullTransactionCodeModel.code());
    }
}