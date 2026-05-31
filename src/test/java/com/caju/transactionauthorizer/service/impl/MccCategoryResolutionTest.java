package com.caju.transactionauthorizer.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Parameterized tests for MCC-to-category resolution logic.
 *
 * <p>Validates all MCC mapping rules across every code defined in the challenge spec,
 * including null and unmapped MCC fallback scenarios.</p>
 */
@DisplayName("MCC Category Resolution — Parameterized Tests")
@ExtendWith(MockitoExtension.class)
class MccCategoryResolutionTest {

    @Mock
    private MerchantCategoryCodesRepository repository;

    private MerchantCategoryCodesService service;

    @BeforeEach
    void setUp() {
        service = new MerchantCategoryCodesServiceImpl(repository);
    }

    @ParameterizedTest(name = "MCC [{0}] should resolve to category [{1}]")
    @CsvSource({
            "5411, FOOD",
            "5412, FOOD",
            "5811, MEAL",
            "5812, MEAL",
            "5999, CASH",
            "4111, CASH",
            "0000, CASH",
            "9999, CASH"
    })
    @DisplayName("checkCategory should map each MCC to the correct category")
    void shouldMapMccToCorrectCategory(String mcc, CategoryCodeName expected) {
        CategoryCodeName category = switch (expected) {
            case FOOD -> CategoryCodeName.FOOD;
            case MEAL -> CategoryCodeName.MEAL;
            case CASH -> CategoryCodeName.CASH;
        };

        if (category != CategoryCodeName.CASH) {
            when(repository.findByCode(mcc))
                    .thenReturn(Optional.of(new MerchantCategoryCodesDocument("id", mcc, category)));
        } else {
            when(repository.findByCode(mcc)).thenReturn(Optional.empty());
        }

        CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "null MCC should resolve to CASH")
    @NullSource
    @DisplayName("checkCategory should return CASH when MCC is null")
    void shouldReturnCashWhenMccIsNull(String mcc) {
        CategoryCodeName result = service.checkCategory(mcc);
        assertEquals(CategoryCodeName.CASH, result);
    }
}
