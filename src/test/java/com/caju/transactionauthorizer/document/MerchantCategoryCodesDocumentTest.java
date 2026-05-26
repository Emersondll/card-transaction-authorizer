package com.caju.transactionauthorizer.document;

import com.caju.transactionauthorizer.enums.CategoryCodeName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MerchantCategoryCodesDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        final MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getCode());
        assertEquals(CategoryCodeName.FOOD, obj.getDescription());
    }

    @Test
    void setId_getId_roundTrip() {
        final MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setCode_getCode_roundTrip() {
        final MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setCode("sampleB");
        assertEquals("sampleB", obj.getCode());
    }

    @Test
    void setDescription_getDescription_roundTrip() {
        final MerchantCategoryCodesDocument obj = new MerchantCategoryCodesDocument("sampleA", "sampleA", CategoryCodeName.FOOD);
        obj.setDescription(CategoryCodeName.FOOD);
        assertEquals(CategoryCodeName.FOOD, obj.getDescription());
    }

}
