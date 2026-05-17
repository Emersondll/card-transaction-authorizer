package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class BalanceDocumentTest {

    private BalanceDocument balanceDocument;

    @BeforeEach
    void setUp() {
        // Initialize a fresh instance before each test
        balanceDocument = new BalanceDocument();
    }

    @Test
    void testDefaultConstructorAndInitialState() {
        // Verify that the default constructor initializes the object correctly (all fields should be null/default)
        assertNull(balanceDocument.getId());
        assertNull(balanceDocument.getAccount());
        assertNull(balanceDocument.getFood());
        assertNull(balanceDocument.getMeal());
        assertNull(balanceDocument.getCash());
        assertNull(balanceDocument.getVersion());
    }

    @Test
    void testFullConstructorAndInitialization() {
        // Setup test data
        final String id = "testId";
        final String account = "ACC123";
        final BigDecimal food = new BigDecimal("10.50");
        final BigDecimal meal = new BigDecimal("25.00");
        final BigDecimal cash = new BigDecimal("5.00");
        final Long version = 1L;

        // Act: Use the full constructor
        final BalanceDocument doc = new BalanceDocument(id, account, food, meal, cash, version);

        // Assert
        assertEquals(id, doc.getId());
        assertEquals(account, doc.getAccount());
        assertEquals(food, doc.getFood());
        assertEquals(meal, doc.getMeal());
        assertEquals(cash, doc.getCash());
        assertEquals(version, doc.getVersion());
    }

    @Test
    void testGettersAndSettersForId() {
        final String newId = "newId";
        balanceDocument.setId(newId);
        assertEquals(newId, balanceDocument.getId());
    }

    @Test
    void testGettersAndSettersForAccount() {
        final String newAccount = "newAccount";
        balanceDocument.setAccount(newAccount);
        assertEquals(newAccount, balanceDocument.getAccount());
    }

    @Test
    void testGettersAndSettersForFood() {
        final BigDecimal foodAmount = new BigDecimal("100.00");
        balanceDocument.setFood(foodAmount);
        assertEquals(foodAmount, balanceDocument.getFood());
    }

    @Test
    void testGettersAndSettersForMeal() {
        final BigDecimal mealAmount = new BigDecimal("50.00");
        balanceDocument.setMeal(mealAmount);
        assertEquals(mealAmount, balanceDocument.getMeal());
    }

    @Test
    void testGettersAndSettersForCash() {
        final BigDecimal cashAmount = new BigDecimal("20.00");
        balanceDocument.setCash(cashAmount);
        assertEquals(cashAmount, balanceDocument.getCash());
    }

    @Test
    void testVersionManagement() {
        final Long initialVersion = 1L;
        balanceDocument.setVersion(initialVersion);
        assertEquals(initialVersion, balanceDocument.getVersion());

        final Long nextVersion = 2L;
        balanceDocument.setVersion(nextVersion);
        assertEquals(nextVersion, balanceDocument.getVersion());
    }

    @Test
    void testSettingNullValues() {
        // Test setting null for all fields to ensure the setters handle it gracefully
        balanceDocument.setId(null);
        balanceDocument.setAccount(null);
        balanceDocument.setFood(null);
        balanceDocument.setMeal(null);
        balanceDocument.setCash(null);
        balanceDocument.setVersion(null);

        assertNull(balanceDocument.getId());
        assertNull(balanceDocument.getAccount());
        assertNull(balanceDocument.getFood());
        assertNull(balanceDocument.getMeal());
        assertNull(balanceDocument.getCash());
        assertNull(balanceDocument.getVersion());
    }
}