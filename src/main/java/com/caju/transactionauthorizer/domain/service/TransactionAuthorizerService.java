package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.*;
import com.caju.transactionauthorizer.domain.port.in.AuthorizeTransactionPort;
import com.caju.transactionauthorizer.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionAuthorizerService implements AuthorizeTransactionPort {

    private final BalanceRepositoryPort balancePort;
    private final MerchantRepositoryPort merchantPort;
    private final MerchantCategoryRepositoryPort categoryPort;
    private final TransactionRepositoryPort transactionPort;
    private final BalanceDeductionService deductionService;

    public TransactionAuthorizerService(
            BalanceRepositoryPort balancePort,
            MerchantRepositoryPort merchantPort,
            MerchantCategoryRepositoryPort categoryPort,
            TransactionRepositoryPort transactionPort,
            BalanceDeductionService deductionService) {
        this.balancePort = balancePort;
        this.merchantPort = merchantPort;
        this.categoryPort = categoryPort;
        this.transactionPort = transactionPort;
        this.deductionService = deductionService;
    }

    @Override
    @Transactional
    public AuthorizationStatus authorize(String accountId, BigDecimal amount, String mcc, String merchant) {
        try {
            Optional<Balance> balanceOpt = balancePort.findByAccount(accountId);
            if (balanceOpt.isEmpty()) {
                return AuthorizationStatus.PROCESSING_ERROR;
            }

            Balance balance = balanceOpt.get();
            String resolvedMcc = resolveMcc(merchant, mcc);
            CategoryCode category = resolveCategory(resolvedMcc);

            boolean deducted = deductionService.deduct(balance, category, amount);
            if (!deducted) {
                return AuthorizationStatus.INSUFFICIENT_FUNDS;
            }

            balancePort.save(balance);
            transactionPort.save(accountId, amount, merchant, resolvedMcc);
            return AuthorizationStatus.APPROVED;

        } catch (Exception e) {
            return AuthorizationStatus.PROCESSING_ERROR;
        }
    }

    private String resolveMcc(String merchant, String mcc) {
        return merchantPort.findByName(merchant)
                .map(Merchant::mcc)
                .orElse(mcc);
    }

    private CategoryCode resolveCategory(String mcc) {
        if (mcc == null) {
            return CategoryCode.CASH;
        }
        return categoryPort.findByCode(mcc)
                .map(MerchantCategory::category)
                .orElse(CategoryCode.CASH);
    }
}
