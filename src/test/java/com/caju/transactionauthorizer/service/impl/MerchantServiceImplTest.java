package com.caju.transactionauthorizer.service.impl;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;
import com.caju.transactionauthorizer.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import com.caju.transactionauthorizer.service.impl.MerchantServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceImplTest {

    @Mock
    private MerchantRepository repository;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @BeforeEach
    public void setUp() {
        // Setup any common state or mocks here if needed
    }

    @Test
    public void testFindByName_MerchantExists_ReturnsMerchantDocument() {
        String merchantName = "TestMerchant";
        MerchantDocument expectedDocument = new MerchantDocument("1", merchantName, "12345");
        Mockito.when(repository.findByName(merchantName)).thenReturn(Optional.of(expectedDocument));

        Optional<MerchantDocument> result = merchantService.findByName(merchantName);

        assertTrue(result.isPresent());
        assertEquals(expectedDocument, result.get());
    }

    @Test
    public void testFindByName_MerchantDoesNotExist_ReturnsEmptyOptional() {
        String merchantName = "NonExistentMerchant";
        Mockito.when(repository.findByName(merchantName)).thenReturn(Optional.empty());

        Optional<MerchantDocument> result = merchantService.findByName(merchantName);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByName_NullInput_ReturnsEmptyOptional() {
        Optional<MerchantDocument> result = merchantService.findByName(null);

        assertFalse(result.isPresent());
    }
}