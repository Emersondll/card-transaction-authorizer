package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TransactionDocumentTest {

    private TransactionDocument document;

    @BeforeEach
    void setUp() {
        // Initialize a standard document instance for testing
        document = new TransactionDocument(
                "doc-id-123",
                "acc-456",
                new BigDecimal("100.50"),
                "Merchant A",
                "5411",
                Timestamp.valueOf("2023-10-27 10:00:00")
        );
    }

    @Test
    void testConstructorAndGetters() {
        // Verify that the document is correctly initialized via the constructor
        assertEquals("doc-id-123", document.getId());
        assertEquals("acc-456", document.getAccountId());
        assertEquals(new BigDecimal("100.50"), document.getAmount());
        assertEquals("Merchant A", document.getMerchant());
        assertEquals("5411", document.getMcc());
        assertEquals(Timestamp.valueOf("2023-10-27 10:00:00"), document.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        // Test setting and retrieving values for all fields
        final String newId = "new-id";
        final String newAccountId = "new-account";
        final BigDecimal newAmount = new BigDecimal("25.00");
        final String newMerchant = "New Merchant";
        final String newMcc = "5812";
        final Timestamp newTimestamp = Timestamp.valueOf("2024-01-01 12:00:00");

        document.setId(newId);
        document.setAccountId(newAccountId);
        document.setAmount(newAmount);
        document.setMerchant(newMerchant);
        document.setMcc(newMcc);
        document.setTimestamp(newTimestamp);

        assertEquals(newId, document.getId());
        assertEquals(newAccountId, document.getAccountId());
        assertEquals(newAmount, document.getAmount());
        assertEquals(newMerchant, document.getMerchant());
        assertEquals(newMcc, document.getMcc());
        assertEquals(newTimestamp, document.getTimestamp());
    }

    @Test
    void testEdgeCaseNullValues() {
        // Test setting null values for all fields
        document.setId(null);
        document.setAccountId(null);
        document.setAmount(null);
        document.setMerchant(null);
        document.setMcc(null);
        document.setTimestamp(null);

        assertNull(document.getId());
        assertNull(document.getAccountId());
        assertNull(document.getAmount());
        assertNull(document.getMerchant());
        assertNull(document.getMcc());
        assertNull(document.getTimestamp());
    }

    @Test
    void testEdgeCaseZeroAmount() {
        // Test setting zero amount
        document.setAmount(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, document.getAmount());
    }

    @Test
    void testEdgeCaseEmptyStrings() {
        // Test setting empty strings
        document.setMerchant("");
        document.setMcc("");

        assertEquals("", document.getMerchant());
        assertEquals("", document.getMcc());
    }
}