package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.Merchant;
import java.util.Optional;

public interface MerchantRepositoryPort {
    Optional<Merchant> findByName(String name);
}
