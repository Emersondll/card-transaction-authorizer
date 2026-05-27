package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BalanceDeductionServiceTest {

    private final BalanceDeductionService service = new BalanceDeductionService();

    @Test
    void deduct_foodWithSufficientFood_deductsFoodReturnsTrue() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertTrue(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getFood());
    }

    @Test
    void deduct_mealWithSufficientMeal_deductsMealReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, new BigDecimal("100.00"), BigDecimal.ZERO, 1L);
        assertTrue(service.deduct(balance, CategoryCode.MEAL, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getMeal());
    }

    @Test
    void deduct_cashWithSufficientCash_deductsCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.CASH, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
    }

    @Test
    void deduct_foodInsufficientButCashSufficient_fallbackToCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
        assertEquals(BigDecimal.ZERO, balance.getFood());
    }

    @Test
    void deduct_mealInsufficientButCashSufficient_fallbackToCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.MEAL, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
    }

    @Test
    void deduct_allInsufficientNoFallback_returnsFalse() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
    }

    @Test
    void deduct_cashCategoryInsufficientNoCashFallback_returnsFalse() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(service.deduct(balance, CategoryCode.CASH, new BigDecimal("50.00")));
    }
}
