package com.caju.transactionauthorizer.model;

import org.junit.jupiter.api.Test;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionCodeModelTest {

    @Test
    public void testTransactionCodeModel() {
        // Test with a valid non-null code
        String validCode = "12345";
        TransactionCodeModel transactionCodeModel = new TransactionCodeModel(validCode);
        assertEquals(validCode, transactionCodeModel.code());

        // Test with null code
        TransactionCodeModel nullTransactionCodeModel = new TransactionCodeModel(null);
        assertNull(nullTransactionCodeModel.code());
    }
}