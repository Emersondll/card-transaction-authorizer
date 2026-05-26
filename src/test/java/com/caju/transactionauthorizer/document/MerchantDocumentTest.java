package com.caju.transactionauthorizer.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MerchantDocumentTest {

    @Test
    void constructor_shouldInitializeAllFields() {
        final MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        assertEquals("sampleA", obj.getId());
        assertEquals("sampleA", obj.getName());
        assertEquals("sampleA", obj.getMcc());
    }

    @Test
    void setId_getId_roundTrip() {
        final MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setId("sampleB");
        assertEquals("sampleB", obj.getId());
    }

    @Test
    void setName_getName_roundTrip() {
        final MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setName("sampleB");
        assertEquals("sampleB", obj.getName());
    }

    @Test
    void setMcc_getMcc_roundTrip() {
        final MerchantDocument obj = new MerchantDocument("sampleA", "sampleA", "sampleA");
        obj.setMcc("sampleB");
        assertEquals("sampleB", obj.getMcc());
    }

}
