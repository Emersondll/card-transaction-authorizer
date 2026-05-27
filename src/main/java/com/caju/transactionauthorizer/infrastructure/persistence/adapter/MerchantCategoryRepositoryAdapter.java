package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import com.caju.transactionauthorizer.domain.port.out.MerchantCategoryRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantCategoryMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MerchantCategoryRepositoryAdapter implements MerchantCategoryRepositoryPort {

    private final MerchantCategoryMongoRepository repository;

    public MerchantCategoryRepositoryAdapter(MerchantCategoryMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MerchantCategory> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return repository.findByCode(code)
                .map(doc -> new MerchantCategory(doc.id(), doc.code(), doc.description()));
    }
}
