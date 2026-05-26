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
import com.caju.transactionauthorizer.service.impl.MerchantCategoryCodesServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        String mcc = "12345";
        MerchantCategoryCodesDocument document = new MerchantCategoryCodesDocument("sampleA", "sampleB", CategoryCodeName.FOOD);
        document.setCode(mcc);
        document.setDescription(CategoryCodeName.FOOD);

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.of(document));

        Optional<MerchantCategoryCodesDocument> result = service.findByCode(mcc);

        assertEquals(document, result.get());
    }

    @Test
    public void testFindByCode_NonExistingMcc() {
        String mcc = "98765";

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.empty());

        Optional<MerchantCategoryCodesDocument> result = service.findByCode(mcc);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testCheckCategory_ExistingMcc() {
        String mcc = "12345";
        MerchantCategoryCodesDocument document = new MerchantCategoryCodesDocument("sampleA", "sampleB", CategoryCodeName.FOOD);
        document.setCode(mcc);
        document.setDescription(CategoryCodeName.FOOD);

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.of(document));

        CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.FOOD, result);
    }

    @Test
    public void testCheckCategory_NonExistingMcc() {
        String mcc = "98765";

        Mockito.when(repository.findByCode(mcc)).thenReturn(Optional.empty());

        CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.CASH, result);
    }

    @Test
    public void testCheckCategory_NullMcc() {
        String mcc = null;

        CategoryCodeName result = service.checkCategory(mcc);

        assertEquals(CategoryCodeName.CASH, result);
    }
}