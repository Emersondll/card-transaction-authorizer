package com.caju.transactionauthorizer.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MerchantCategoryCodesServiceImpl}.
 */
@DisplayName("MerchantCategoryCodesServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class MerchantCategoryCodesServiceImplTest {

    @Mock
    private MerchantCategoryCodesRepository repository;

    private MerchantCategoryCodesService service;

    @BeforeEach
    void setUp() {
        service = new MerchantCategoryCodesServiceImpl(repository);
    }

    @Test
    @DisplayName("checkCategory should return FOOD for MCC 5411")
    void shouldReturnFoodForMcc5411() {
        when(repository.findByCode("5411"))
                .thenReturn(Optional.of(new MerchantCategoryCodesDocument("1", "5411", CategoryCodeName.FOOD)));

        CategoryCodeName result = service.checkCategory("5411");

        assertEquals(CategoryCodeName.FOOD, result);
    }

    @Test
    @DisplayName("checkCategory should return MEAL for MCC 5811")
    void shouldReturnMealForMcc5811() {
        when(repository.findByCode("5811"))
                .thenReturn(Optional.of(new MerchantCategoryCodesDocument("2", "5811", CategoryCodeName.MEAL)));

        CategoryCodeName result = service.checkCategory("5811");

        assertEquals(CategoryCodeName.MEAL, result);
    }

    @Test
    @DisplayName("checkCategory should return CASH when MCC is not in mapping table")
    void shouldReturnCashWhenMccNotMapped() {
        when(repository.findByCode("9999")).thenReturn(Optional.empty());

        CategoryCodeName result = service.checkCategory("9999");

        assertEquals(CategoryCodeName.CASH, result);
    }

    @Test
    @DisplayName("checkCategory should return CASH when MCC is null")
    void shouldReturnCashWhenMccIsNull() {
        CategoryCodeName result = service.checkCategory(null);

        assertEquals(CategoryCodeName.CASH, result);
    }

    @Test
    @DisplayName("findByCode should return document when MCC exists")
    void shouldReturnDocumentWhenMccExists() {
        MerchantCategoryCodesDocument doc = new MerchantCategoryCodesDocument("1", "5412", CategoryCodeName.FOOD);
        when(repository.findByCode("5412")).thenReturn(Optional.of(doc));

        Optional<MerchantCategoryCodesDocument> result = service.findByCode("5412");

        assertEquals(Optional.of(doc), result);
    }
}
