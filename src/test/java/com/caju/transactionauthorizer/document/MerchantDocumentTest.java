package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MerchantDocumentTest {

    @InjectMocks
    private MerchantDocument merchantDocument;

    private String testId;
    private String testName;
    private String testMcc;

    @BeforeEach
    void setUp() {
        testId = "doc-123";
        testName = "Test Merchant Corp";
        testMcc = "5411";
    }

    @Test
    void constructor_shouldInitializeAllFieldsCorrectly() {
        // Act
        final MerchantDocument document = new MerchantDocument(testId, testName, testMcc);

        // Assert
        assertEquals(testId, document.getId());
        assertEquals(testName, document.getName());
        assertEquals(testMcc, document.getMcc());
    }

    @Test
    void getId_shouldReturnCorrectId() {
        // Arrange
        merchantDocument.setId(testId);

        // Act & Assert
        assertEquals(testId, merchantDocument.getId());
    }

    @Test
    void setId_shouldUpdateId() {
        // Arrange
        merchantDocument.setId("old-id");

        // Act
        merchantDocument.setId("new-id");

        // Assert
        assertEquals("new-id", merchantDocument.getId());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        // Arrange
        merchantDocument.setName(testName);

        // Act & Assert
        assertEquals(testName, merchantDocument.getName());
    }

    @Test
    void setName_shouldUpdateName() {
        // Arrange
        merchantDocument.setName("old-name");

        // Act
        merchantDocument.setName("new-name");

        // Assert
        assertEquals("new-name", merchantDocument.getName());
    }

    @Test
    void getMcc_shouldReturnCorrectMcc() {
        // Arrange
        merchantDocument.setMcc(testMcc);

        // Act & Assert
        assertEquals(testMcc, merchantDocument.getMcc());
    }

    @Test
    void setMcc_shouldUpdateMcc() {
        // Arrange
        merchantDocument.setMcc("old-mcc");

        // Act
        merchantDocument.setMcc("new-mcc");

        // Assert
        assertEquals("new-mcc", merchantDocument.getMcc());
    }

    @Test
    void gettersAndSetters_shouldHandleNullValues() {
        // Test ID
        merchantDocument.setId(null);
        assertEquals(null, merchantDocument.getId());

        // Test Name
        merchantDocument.setName(null);
        assertEquals(null, merchantDocument.getName());

        // Test MCC
        merchantDocument.setMcc(null);
        assertEquals(null, merchantDocument.getMcc());
    }
}