package com.caju.transactionauthorizer.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.repository.MerchantRepository;
import com.caju.transactionauthorizer.service.MerchantService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MerchantServiceImpl}.
 */
@DisplayName("MerchantServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class MerchantServiceImplTest {

    @Mock
    private MerchantRepository repository;

    private MerchantService service;

    @BeforeEach
    void setUp() {
        service = new MerchantServiceImpl(repository);
    }

    @Test
    @DisplayName("findByName should return merchant document when name matches override table")
    void shouldReturnMerchantWhenNameFound() {
        MerchantDocument doc = new MerchantDocument("1", "UBER EATS  SAO PAULO BR", "5811");
        when(repository.findByName("UBER EATS  SAO PAULO BR")).thenReturn(Optional.of(doc));

        Optional<MerchantDocument> result = service.findByName("UBER EATS  SAO PAULO BR");

        assertTrue(result.isPresent());
        assertEquals("5811", result.get().getMcc());
    }

    @Test
    @DisplayName("findByName should return empty when merchant has no override")
    void shouldReturnEmptyWhenNoOverrideExists() {
        when(repository.findByName("UNKNOWN MERCHANT")).thenReturn(Optional.empty());

        Optional<MerchantDocument> result = service.findByName("UNKNOWN MERCHANT");

        assertTrue(result.isEmpty());
    }
}
