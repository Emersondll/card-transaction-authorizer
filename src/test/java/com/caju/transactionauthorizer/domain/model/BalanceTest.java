package com.caju.transactionauthorizer.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BalanceTest {

    @Test
    void getters_returnCorrectValues() {
        Balance b = new Balance("id", "acc", new BigDecimal("10"), new BigDecimal("20"), new BigDecimal("30"), 1L);
        assertEquals("id", b.getId());
        assertEquals("acc", b.getAccount());
        assertEquals(new BigDecimal("10"), b.getFood());
        assertEquals(new BigDecimal("20"), b.getMeal());
        assertEquals(new BigDecimal("30"), b.getCash());
        assertEquals(1L, b.getVersion());
    }

    @Test
    void deductFrom_foodSufficient_returnsTrueAndUpdatesFood() {
        Balance b = new Balance("id", "acc", new BigDecimal("100"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertTrue(b.deductFrom(CategoryCode.FOOD, new BigDecimal("60")));
        assertEquals(new BigDecimal("40"), b.getFood());
    }

    @Test
    void deductFrom_mealSufficient_returnsTrueAndUpdatesMeal() {
        Balance b = new Balance("id", "acc", BigDecimal.ZERO, new BigDecimal("100"), BigDecimal.ZERO, 1L);
        assertTrue(b.deductFrom(CategoryCode.MEAL, new BigDecimal("30")));
        assertEquals(new BigDecimal("70"), b.getMeal());
    }

    @Test
    void deductFrom_cashSufficient_returnsTrueAndUpdatesCash() {
        Balance b = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100"), 1L);
        assertTrue(b.deductFrom(CategoryCode.CASH, new BigDecimal("40")));
        assertEquals(new BigDecimal("60"), b.getCash());
    }

    @Test
    void deductFrom_insufficient_returnsFalseAndDoesNotUpdate() {
        Balance b = new Balance("id", "acc", new BigDecimal("10"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(b.deductFrom(CategoryCode.FOOD, new BigDecimal("50")));
        assertEquals(new BigDecimal("10"), b.getFood());
    }

    @Test
    void deductCash_sufficient_returnsTrueAndUpdatesCash() {
        Balance b = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100"), 1L);
        assertTrue(b.deductCash(new BigDecimal("25")));
        assertEquals(new BigDecimal("75"), b.getCash());
    }

    @Test
    void deductCash_insufficient_returnsFalse() {
        Balance b = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(b.deductCash(new BigDecimal("10")));
    }

    @Test
    void deductFrom_exactAmount_returnsTrue() {
        Balance b = new Balance("id", "acc", new BigDecimal("50"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertTrue(b.deductFrom(CategoryCode.FOOD, new BigDecimal("50")));
        assertEquals(BigDecimal.ZERO, b.getFood());
    }
}
