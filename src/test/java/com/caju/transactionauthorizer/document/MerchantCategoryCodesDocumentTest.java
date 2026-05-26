package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;

class MerchantCategoryCodesDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getCode());
        assertEquals(CategoryCodeName.FOOD, obj.getDescription());
    }

    @Test
    void setId_getId_roundTrip() {
        MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setCode_getCode_roundTrip() {
        MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setCode("sampleB");
        assertEquals("sampleB", obj.getCode());
    }

    @Test
    void setDescription_getDescription_roundTrip() {
        MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setDescription(CategoryCodeName.FOOD);
        assertEquals(CategoryCodeName.FOOD, obj.getDescription());
    }

}
