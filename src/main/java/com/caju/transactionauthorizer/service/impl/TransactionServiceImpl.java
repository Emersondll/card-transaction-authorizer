package com.caju.transactionauthorizer.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantCategoryCodesService categoryCodesService;

    /**
     * Performs a transaction and returns the result code.
     *
     * @param transactionModel the model containing details of the transaction
     * @return the transaction code indicating success or failure
     */
    @Override
    @Transactional
    public TransactionCodeModel performTransaction(final TransactionModel transactionModel) {
        final String accountId = transactionModel.account();
        final BigDecimal amount = transactionModel.totalAmount();

        try {
            final Optional<BalanceDocument> balanceOptional = balanceService.findByAccount(accountId);
            if (balanceOptional.isEmpty()) {
                return new TransactionCodeModel(TransactionStatusCode.PROCESSING_ERROR.getCode());
            }

            final BalanceDocument balance = balanceOptional.get();
            final String mccCategoryNumber = determineMccCategory(transactionModel);
            final CategoryCodeName categoryCodeName = categoryCodesService.checkCategory(mccCategoryNumber);

            final TransactionCodeModel transactionCodeModel = updateWalletBalance(balance, categoryCodeName, amount);
            if (transactionCodeModel.code().equals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode())) {
                return transactionCodeModel;
            }

            balanceService.save(balance);
            saveTransaction(transactionModel, accountId, amount, mccCategoryNumber);

            return transactionCodeModel;
        } catch (Exception e) {
            return new TransactionCodeModel(TransactionStatusCode.PROCESSING_ERROR.getCode());
        }
    }

    /**
     * Determines the MCC category number for a given transaction.
     *
     * @param transactionModel the model containing details of the transaction
     * @return the MCC category number
     */
    protected String determineMccCategory(final TransactionModel transactionModel) {
        final Optional<MerchantDocument> merchantOptional = merchantService.findByName(transactionModel.merchant());
        if (merchantOptional.isPresent()) {
            return merchantOptional.get().getMcc();
        } else {
            final Optional<MerchantCategoryCodesDocument> categoryCodesOptional = categoryCodesService.findByCode(transactionModel.mcc());
            return categoryCodesOptional.map(MerchantCategoryCodesDocument::getCode).orElse(null);
        }
    }

    /**
     * Updates the wallet balance based on the transaction details.
     *
     * @param balance the balance document
     * @param categoryCodeName the category code name
     * @param amount the transaction amount
     * @return the transaction code indicating success or failure
     */
    protected TransactionCodeModel updateWalletBalance(final BalanceDocument balance, final CategoryCodeName categoryCodeName, final BigDecimal amount) {
        final boolean updated = switch (categoryCodeName) {
            case FOOD ->
                updateBalanceWithFallback(balance::getFood, balance::setFood, balance::getCash, balance::setCash, amount);
            case MEAL ->
                updateBalanceWithFallback(balance::getMeal, balance::setMeal, balance::getCash, balance::setCash, amount);
            case CASH ->
                updateBalanceWithFallback(balance::getCash, balance::setCash, balance::getCash, balance::setCash, amount);
        };

        if (!updated) {
            return new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode());
        }

        return new TransactionCodeModel(TransactionStatusCode.APPROVED.getCode());
    }

    /**
     * Updates the balance with fallback logic.
     *
     * @param primaryGetter the getter for the primary balance
     * @param primarySetter the setter for the primary balance
     * @param fallbackGetter the getter for the fallback balance
     * @param fallbackSetter the setter for the fallback balance
     * @param amount the transaction amount
     * @return true if the update was successful, false otherwise
     */
    protected boolean updateBalanceWithFallback(final Supplier<BigDecimal> primaryGetter, final Consumer<BigDecimal> primarySetter,
                                                final Supplier<BigDecimal> fallbackGetter, final Consumer<BigDecimal> fallbackSetter, final BigDecimal amount) {

        final BigDecimal primaryBalance = primaryGetter.get();
        final BigDecimal updatedPrimaryBalance = subtractAmount(primaryBalance, amount);

        if (updatedPrimaryBalance != null) {
            primarySetter.accept(updatedPrimaryBalance);
            return true;
        }

        final BigDecimal fallbackBalance = fallbackGetter.get();
        final BigDecimal updatedFallbackBalance = subtractAmount(fallbackBalance, amount);

        if (updatedFallbackBalance != null) {
            fallbackSetter.accept(updatedFallbackBalance);
            return true;
        }

        return false;
    }

    /**
     * Subtracts the transaction amount from the balance.
     *
     * @param balance the current balance
     * @param amount the transaction amount
     * @return the updated balance if sufficient funds, null otherwise
     */
    private BigDecimal subtractAmount(final BigDecimal balance, final BigDecimal amount) {
        final BigDecimal result = balance.subtract(amount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? null : result; } /** * Saves the transaction details to the repository. * * @param transactionModel the model containing details of the transaction * @param accountId the account ID * @param amount the transaction amount * @param mccCategoryNumber the MCC category number */ protected void saveTransaction(final TransactionModel transactionModel, final String accountId, final BigDecimal amount, final String mccCategoryNumber) { final TransactionDocument transactionDocument = new TransactionDocument( UUID.randomUUID().toString(), accountId, amount, transactionModel.merchant(), mccCategoryNumber, Timestamp.valueOf(LocalDateTime.now())); transactionRepository.save(transactionDocument); } }