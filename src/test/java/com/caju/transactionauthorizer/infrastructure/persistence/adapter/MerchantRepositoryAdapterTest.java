package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Merchant;
import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MerchantRepositoryAdapterTest {

    @Mock MerchantMongoRepository repository;
    @InjectMocks MerchantRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByName_found_returnsMappedMerchant() {
        when(repository.findByName("PADARIA")).thenReturn(Optional.of(new MerchantDocument("id", "PADARIA", "5411")));
        Optional<Merchant> result = adapter.findByName("PADARIA");
        assertTrue(result.isPresent());
        assertEquals("5411", result.get().mcc());
        assertEquals("PADARIA", result.get().name());
    }

    @Test
    void findByName_notFound_returnsEmpty() {
        when(repository.findByName("PADARIA")).thenReturn(Optional.empty());
        assertTrue(adapter.findByName("PADARIA").isEmpty());
    }
}
