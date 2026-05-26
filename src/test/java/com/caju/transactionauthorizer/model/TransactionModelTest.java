package com.caju.transactionauthorizer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TransactionModelTest {

    private TransactionModel transactionModel;

    @BeforeEach
    public void setUp() {
        transactionModel = new TransactionModel("123456", BigDecimal.valueOf(100), "5411", "Merchant Name");
    }

    @Test
    public void testTransactionModelConstructor() {
        assertEquals("123456", transactionModel.account());
        assertEquals(BigDecimal.valueOf(100), transactionModel.totalAmount());
        assertEquals("5411", transactionModel.mcc());
        assertEquals("Merchant Name", transactionModel.merchant());
    }

    @Test
    public void testTransactionModelEquals() {
        final TransactionModel sameTransaction = new TransactionModel("123456", BigDecimal.valueOf(100), "5411", "Merchant Name");
        final TransactionModel differentTransaction = new TransactionModel("654321", BigDecimal.valueOf(200), "1145", "Different Merchant");

        assertEquals(transactionModel, sameTransaction);
        assertNotEquals(transactionModel, differentTransaction);
    }

    @Test
    public void testTransactionModelHashCode() {
        final TransactionModel sameTransaction = new TransactionModel("123456", BigDecimal.valueOf(100), "5411", "Merchant Name");

        assertEquals(transactionModel.hashCode(), sameTransaction.hashCode());
    }
}