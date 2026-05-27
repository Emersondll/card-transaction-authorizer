package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.CategoryCode;
import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantCategoryDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantCategoryMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MerchantCategoryRepositoryAdapterTest {

    @Mock MerchantCategoryMongoRepository repository;
    @InjectMocks MerchantCategoryRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByCode_nullCode_returnsEmptyWithoutQueryingDb() {
        Optional<MerchantCategory> result = adapter.findByCode(null);
        assertTrue(result.isEmpty());
        verify(repository, never()).findByCode(any());
    }

    @Test
    void findByCode_found_returnsMappedCategory() {
        when(repository.findByCode("5411")).thenReturn(
                Optional.of(new MerchantCategoryDocument("id", "5411", CategoryCode.FOOD)));
        Optional<MerchantCategory> result = adapter.findByCode("5411");
        assertTrue(result.isPresent());
        assertEquals(CategoryCode.FOOD, result.get().category());
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(repository.findByCode("9999")).thenReturn(Optional.empty());
        assertTrue(adapter.findByCode("9999").isEmpty());
    }
}
