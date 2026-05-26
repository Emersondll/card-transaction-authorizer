package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.caju.transactionauthorizer.document.MerchantDocument;

class MerchantDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getName());
        assertEquals("sampleA", obj.getMcc());
    }

    @Test
    void setId_getId_roundTrip() {
        MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setName_getName_roundTrip() {
        MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setName("sampleB");
        assertEquals("sampleB", obj.getName());
    }

    @Test
    void setMcc_getMcc_roundTrip() {
        MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setMcc("sampleB");
        assertEquals("sampleB", obj.getMcc());
    }

}
