package com.caju.transactionauthorizer.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caju.transactionauthorizer.document.BalanceDocument;
import com.caju.transactionauthorizer.document.MerchantCategoryCodesDocument;
import com.caju.transactionauthorizer.document.MerchantDocument;
import com.caju.transactionauthorizer.document.TransactionDocument;
import com.caju.transactionauthorizer.enums.CategoryCodeName;
import com.caju.transactionauthorizer.enums.TransactionStatusCode;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.repository.TransactionRepository;
import com.caju.transactionauthorizer.service.BalanceService;
import com.caju.transactionauthorizer.service.MerchantCategoryCodesService;
import com.caju.transactionauthorizer.service.MerchantService;
import com.caju.transactionauthorizer.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic implementation for the complete transaction authorization flow.
 *
 * <p>Implements all four challenge levels:
 * <ul>
 *   <li><b>L1 — Simple authorizer</b>: maps MCC to a benefit category and debits that bucket.</li>
 *   <li><b>L2 — Fallback</b>: if the primary bucket has insufficient funds, tries the CASH bucket.</li>
 *   <li><b>L3 — Merchant precedence</b>: overrides the transaction MCC with the stored merchant MCC
 *       when the merchant name matches a record in the override table.</li>
 *   <li><b>L4 — Concurrency</b>: optimistic locking on {@link BalanceDocument} prevents
 *       lost-update anomalies. Concurrent failures are caught and returned as code {@code "07"}.</li>
 * </ul>
 *
 * <p>Thread Safety: Stateless service; thread-safe as a Spring singleton.</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see BalanceService for balance operations
 * @see MerchantService for merchant override look-up
 * @see MerchantCategoryCodesService for MCC-to-category resolution
 */
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final MerchantService merchantService;
    private final MerchantCategoryCodesService categoryCodesService;

    /**
     * Constructor-based dependency injection.
     *
     * @param transactionRepository repository for persisting transaction audit records (non-null)
     * @param balanceService        service for balance read/write operations (non-null)
     * @param merchantService       service for merchant MCC override look-up (non-null)
     * @param categoryCodesService  service for MCC-to-category resolution (non-null)
     * @throws NullPointerException if any dependency is null
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  BalanceService balanceService,
                                  MerchantService merchantService,
                                  MerchantCategoryCodesService categoryCodesService) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "TransactionRepository cannot be null");
        this.balanceService = Objects.requireNonNull(balanceService, "BalanceService cannot be null");
        this.merchantService = Objects.requireNonNull(merchantService, "MerchantService cannot be null");
        this.categoryCodesService = Objects.requireNonNull(categoryCodesService, "MerchantCategoryCodesService cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p>All exceptions are caught internally and mapped to code {@code "07"}
     * so the HTTP response is always {@code 200 OK}.</p>
     */
    @Override
    @Transactional
    public TransactionCodeModel performTransaction(TransactionModel transactionModel) {
        Objects.requireNonNull(transactionModel, "TransactionModel cannot be null");

        try {
            return processAuthorization(transactionModel);
        } catch (Exception exception) {
            log.error("Transaction processing error. account={}", transactionModel.account(), exception);
            return processingError();
        }
    }

    /**
     * Determines the effective MCC for a transaction, applying the L3 merchant-precedence rule.
     *
     * <p>If the merchant name matches a record in the override table, the stored MCC is used;
     * otherwise falls back to the MCC field from the transaction payload.</p>
     *
     * @param transactionModel the incoming transaction request (non-null)
     * @return the resolved MCC string, or {@code null} if no mapping is found
     */
    protected String determineMccCategory(TransactionModel transactionModel) {
        Optional<MerchantDocument> merchantOverride = merchantService.findByName(transactionModel.merchant());
        if (merchantOverride.isPresent()) {
            log.debug("Merchant override applied. merchant={}, mcc={}", transactionModel.merchant(), merchantOverride.get().getMcc());
            return merchantOverride.get().getMcc();
        }

        return categoryCodesService.findByCode(transactionModel.mcc())
                .map(MerchantCategoryCodesDocument::getCode)
                .orElse(null);
    }

    /**
     * Debits the correct wallet bucket based on the resolved category, with CASH fallback (L2).
     *
     * @param balance          the account balance document to mutate (non-null)
     * @param categoryCodeName the resolved category (non-null)
     * @param amount           the amount to debit (non-null, positive)
     * @return a {@link TransactionCodeModel} with {@code "00"} if approved, {@code "51"} if rejected
     */
    protected TransactionCodeModel updateWalletBalance(BalanceDocument balance,
                                                       CategoryCodeName categoryCodeName,
                                                       BigDecimal amount) {
        boolean debited = switch (categoryCodeName) {
            case FOOD -> updateBalanceWithFallback(balance::getFood, balance::setFood,
                    balance::getCash, balance::setCash, amount);
            case MEAL -> updateBalanceWithFallback(balance::getMeal, balance::setMeal,
                    balance::getCash, balance::setCash, amount);
            case CASH -> updateBalanceWithFallback(balance::getCash, balance::setCash,
                    balance::getCash, balance::setCash, amount);
        };

        return debited
                ? new TransactionCodeModel(TransactionStatusCode.APPROVED.getCode())
                : new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode());
    }

    /**
     * Attempts to debit {@code amount} from the primary balance bucket; falls back to the
     * secondary bucket if the primary has insufficient funds (L2 fallback logic).
     *
     * @param primaryGetter   getter for the primary balance bucket
     * @param primarySetter   setter for the primary balance bucket
     * @param fallbackGetter  getter for the fallback balance bucket
     * @param fallbackSetter  setter for the fallback balance bucket
     * @param amount          the amount to debit
     * @return {@code true} if the debit succeeded in either bucket, {@code false} otherwise
     */
    protected boolean updateBalanceWithFallback(Supplier<BigDecimal> primaryGetter,
                                                Consumer<BigDecimal> primarySetter,
                                                Supplier<BigDecimal> fallbackGetter,
                                                Consumer<BigDecimal> fallbackSetter,
                                                BigDecimal amount) {
        BigDecimal updatedPrimary = subtractIfSufficient(primaryGetter.get(), amount);
        if (updatedPrimary != null) {
            primarySetter.accept(updatedPrimary);
            return true;
        }

        BigDecimal updatedFallback = subtractIfSufficient(fallbackGetter.get(), amount);
        if (updatedFallback != null) {
            fallbackSetter.accept(updatedFallback);
            return true;
        }

        return false;
    }

    /**
     * Persists an audit record of a successfully authorized transaction.
     *
     * @param transactionModel the original request (non-null)
     * @param accountId        the account that was debited (non-null)
     * @param amount           the debited amount (non-null)
     * @param mcc              the effective MCC used for authorization (may be null)
     */
    protected void saveTransaction(TransactionModel transactionModel,
                                   String accountId,
                                   BigDecimal amount,
                                   String mcc) {
        TransactionDocument record = TransactionDocument.builder()
                .id(UUID.randomUUID().toString())
                .accountId(accountId)
                .amount(amount)
                .merchant(transactionModel.merchant())
                .mcc(mcc)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(record);
        log.info("Transaction persisted. accountId={}, amount={}, mcc={}", accountId, amount, mcc);
    }

    // ---- private helpers ----

    private TransactionCodeModel processAuthorization(TransactionModel transactionModel) {
        String accountId = transactionModel.account();
        BigDecimal amount = transactionModel.totalAmount();

        Optional<BalanceDocument> balanceOptional = balanceService.findByAccount(accountId);
        if (balanceOptional.isEmpty()) {
            log.warn("Account not found. accountId={}", accountId);
            return processingError();
        }

        BalanceDocument balance = balanceOptional.get();
        String mcc = determineMccCategory(transactionModel);
        CategoryCodeName category = categoryCodesService.checkCategory(mcc);

        log.info("Authorizing transaction. accountId={}, amount={}, mcc={}, category={}", accountId, amount, mcc, category);

        TransactionCodeModel result = updateWalletBalance(balance, category, amount);
        if (result.code().equals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode())) {
            log.info("Transaction rejected — insufficient funds. accountId={}", accountId);
            return result;
        }

        balanceService.save(balance);
        saveTransaction(transactionModel, accountId, amount, mcc);

        return result;
    }

    private TransactionCodeModel processingError() {
        return new TransactionCodeModel(TransactionStatusCode.PROCESSING_ERROR.getCode());
    }

    private BigDecimal subtractIfSufficient(BigDecimal balance, BigDecimal amount) {
        BigDecimal result = balance.subtract(amount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? null : result;
    }
}
