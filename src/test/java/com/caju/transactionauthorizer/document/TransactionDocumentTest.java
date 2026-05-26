package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getAccountId());
        assertEquals(new BigDecimal("100.00"), obj.getAmount());
        assertEquals("sampleA", obj.getMerchant());
        assertEquals("sampleA", obj.getMcc());
        assertEquals(Timestamp.valueOf("2023-01-15 10:00:00"), obj.getTimestamp());
    }

    @Test
    void setId_getId_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setAccountId_getAccountId_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setAccountId("sampleB");
        assertEquals("sampleB", obj.getAccountId());
    }

    @Test
    void setAmount_getAmount_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setAmount(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), obj.getAmount());
    }

    @Test
    void setMerchant_getMerchant_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setMerchant("sampleB");
        assertEquals("sampleB", obj.getMerchant());
    }

    @Test
    void setMcc_getMcc_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setMcc("sampleB");
        assertEquals("sampleB", obj.getMcc());
    }

    @Test
    void setTimestamp_getTimestamp_roundTrip() {
        final TransactionDocument obj = new TransactionDocument("sampleA", "sampleA", new BigDecimal("100.00"), "sampleA", "sampleA", Timestamp.valueOf("2023-01-15 10:00:00"));
        obj.setTimestamp(Timestamp.valueOf("2024-06-20 14:30:00"));
        assertEquals(Timestamp.valueOf("2024-06-20 14:30:00"), obj.getTimestamp());
    }

}
