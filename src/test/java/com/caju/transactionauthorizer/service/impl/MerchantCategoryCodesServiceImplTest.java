package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.repository.MerchantCategoryCodesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MerchantCategoryCodesServiceImplTest {

    @Mock
    private MerchantCategoryCodesRepository repository;

    @InjectMocks
    private MerchantCategoryCodesServiceImpl service;

    @BeforeEach
    public void setUp() {
        // Setup any common mocks or initializations if needed
    }

    @Test
    public void testFindByCode_ExistingMcc() {
        final String mcc = "12345";
        final MerchantCategoryCodesDocument document = new MerchantCategoryCodesDocument("sampleA", "sampleB", CategoryCodeName.FOOD);
        document.setCode(mcc);
        document.setDescription(CategoryCodeName.FOOD);

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.of(document));

        final Optional<MerchantCategoryCodesDocument> result = service.findByCode(mcc);

        assertEquals(document, result.get());
    }

    @Test
    public void testFindByCode_NonExistingMcc() {
        final String mcc = "98765";

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.empty());

        final Optional<MerchantCategoryCodesDocument> result = service.findByCode(mcc);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testCheckCategory_ExistingMcc() {
        final String mcc = "12345";
        final MerchantCategoryCodesDocument document = new MerchantCategoryCodesDocument("sampleA", "sampleB", CategoryCodeName.FOOD);
        document.setCode(mcc);
        document.setDescription(CategoryCodeName.FOOD);

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.of(document));

        final CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.FOOD, result);
    }

    @Test
    public void testCheckCategory_NonExistingMcc() {
        final String mcc = "98765";

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.empty());

        final CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.CASH, result);
    }

    @Test
    public void testCheckCategory_NullMcc() {
        final String mcc = null;

        final CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.CASH, result);
    }
}