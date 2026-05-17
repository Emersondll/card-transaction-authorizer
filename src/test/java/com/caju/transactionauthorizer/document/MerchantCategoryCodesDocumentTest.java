package com.caju.transactionauthorizer.document;

import com.caju.transactionauthorizer.enums.CategoryCodeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class MerchantCategoryCodesDocumentTest {

    @InjectMocks
    private MerchantCategoryCodesDocument document;

    // Test setup variables
    private String testId;
    private String testCode;
    private CategoryCodeName testDescription;

    @BeforeEach
    void setUp() {
        testId = "test-id";
        testCode = "MCC123";
        testDescription = CategoryCodeName.FOOD; // Using an explicit enum constant
    }

    @Test
    void constructor_shouldInitializeAllFields() {
        // Act
        final MerchantCategoryCodesDocument doc = new MerchantCategoryCodesDocument(testId, testCode, testDescription);

        // Assert
        assertEquals(testId, doc.getId());
        assertEquals(testCode, doc.getCode());
        assertEquals(testDescription, doc.getDescription());
    }

    @Test
    void getId_shouldReturnCorrectId() {
        // Act
        final String newId = "new-id";
        document.setId(newId);

        // Assert
        assertEquals(newId, document.getId());
    }

    @Test
    void setId_shouldHandleNullId() {
        // Act
        document.setId(null);

        // Assert
        assertNull(document.getId());
    }

    @Test
    void getCode_shouldReturnCorrectCode() {
        // Act
        final String newCode = "NEWCODE";
        document.setCode(newCode);

        // Assert
        assertEquals(newCode, document.getCode());
    }

    @Test
    void setCode_shouldHandleEmptyCode() {
        // Act
        document.setCode("");

        // Assert
        assertEquals("", document.getCode());
    }

    @Test
    void getDescription_shouldReturnCorrectDescription() {
        // Act
        final CategoryCodeName newDescription = CategoryCodeName.MEAL;
        document.setDescription(newDescription);

        // Assert
        assertEquals(newDescription, document.getDescription());
    }

    @Test
    void setDescription_shouldHandleNullDescription() {
        // Act
        document.setDescription(null);

        // Assert
        assertNull(document.getDescription());
    }

    @Test
    void gettersAndSetters_shouldMaintainConsistency() {
        // Arrange
        final String expectedId = "unique-id";
        final String expectedCode = "UNIQUE";
        final CategoryCodeName expectedDescription = CategoryCodeName.CASH;

        // Act
        document.setId(expectedId);
        document.setCode(expectedCode);
        document.setDescription(expectedDescription);

        // Assert
        assertEquals(expectedId, document.getId());
        assertEquals(expectedCode, document.getCode());
        assertEquals(expectedDescription, document.getDescription());
    }
}