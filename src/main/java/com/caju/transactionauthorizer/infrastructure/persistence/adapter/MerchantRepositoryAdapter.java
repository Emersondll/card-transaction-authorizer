package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Merchant;
import com.caju.transactionauthorizer.domain.port.out.MerchantRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MerchantRepositoryAdapter implements MerchantRepositoryPort {

    private final MerchantMongoRepository repository;

    public MerchantRepositoryAdapter(MerchantMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return repository.findByName(name)
                .map(doc -> new Merchant(doc.id(), doc.name(), doc.mcc()));
    }
}
