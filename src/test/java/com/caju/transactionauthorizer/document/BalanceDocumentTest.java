package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BalanceDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getAccount());
        assertEquals(new BigDecimal("100.00"), obj.getFood());
        assertEquals(new BigDecimal("100.00"), obj.getMeal());
        assertEquals(new BigDecimal("100.00"), obj.getCash());
        assertEquals(1L, obj.getVersion());
    }

    @Test
    void setId_getId_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setAccount_getAccount_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setAccount("sampleB");
        assertEquals("sampleB", obj.getAccount());
    }

    @Test
    void setFood_getFood_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setFood(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), obj.getFood());
    }

    @Test
    void setMeal_getMeal_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setMeal(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), obj.getMeal());
    }

    @Test
    void setCash_getCash_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setCash(new BigDecimal("250.50"));
        assertEquals(new BigDecimal("250.50"), obj.getCash());
    }

    @Test
    void setVersion_getVersion_roundTrip() {
        final BalanceDocument obj = new BalanceDocument("sampleA", "sampleA", new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"), 1L);
        obj.setVersion(2L);
        assertEquals(2L, obj.getVersion());
    }

}
