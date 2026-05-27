package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import java.util.Optional;

public interface MerchantCategoryRepositoryPort {
    Optional<MerchantCategory> findByCode(String code);
}
