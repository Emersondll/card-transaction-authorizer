package com.caju.transactionauthorizer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionModelTest {

    private TransactionModel model;

    @BeforeEach
    void setUp() {
        // Setup a standard, valid instance for testing
        final String account = "ACC123";
        final BigDecimal amount = new BigDecimal("100.50");
        final String mcc = "5411";
        final String merchant = "Starbucks";
        model = new TransactionModel(account, amount, mcc, merchant);
    }

    @Test
    void testConstructionAndAccessors() {
        // Test that the record can be constructed and fields are accessible
        assertEquals("ACC123", model.account());
        assertEquals(new BigDecimal("100.50"), model.totalAmount());
        assertEquals("5411", model.mcc());
        assertEquals("Starbucks", model.merchant());
    }

    @Test
    void testEquality_SameInstances() {
        // Two records with the same data must be equal
        final TransactionModel model2 = new TransactionModel("ACC123", new BigDecimal("100.50"), "5411", "Starbucks");
        assertEquals(model, model2);
    }

    @Test
    void testEquality_DifferentInstances() {
        // Records with different data must not be equal
        final TransactionModel modelDifferent = new TransactionModel("ACC999", new BigDecimal("1.00"), "1234", "OtherMerchant");
        assertNotEquals(model, modelDifferent);
    }

    @Test
    void testToStringFormat() {
        // Test that toString() follows the canonical record format
        final String expectedString = "TransactionModel[account=ACC123, totalAmount=100.50, mcc=5411, merchant=Starbucks]";
        assertEquals(expectedString, model.toString());
    }

    @Test
    void testEdgeCase_ZeroAmount() {
        // Test case with zero amount
        final TransactionModel zeroModel = new TransactionModel("ACC0", BigDecimal.ZERO, "1111", "Test");
        assertEquals(BigDecimal.ZERO, zeroModel.totalAmount());
        assertEquals(zeroModel, new TransactionModel("ACC0", BigDecimal.ZERO, "1111", "Test"));
    }

    @Test
    void testEdgeCase_NullFields() {
        // Test case where some fields might be null (assuming the record allows it)
        // Since the record definition uses String and BigDecimal, null is possible.
        final TransactionModel nullModel = new TransactionModel(null, null, "1234", null);
        assertNull(nullModel.account());
        assertNull(nullModel.totalAmount());
        assertEquals("1234", nullModel.mcc());
        assertNull(nullModel.merchant());
    }
}