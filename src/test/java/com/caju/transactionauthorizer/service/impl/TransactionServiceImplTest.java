package com.caju.transactionauthorizer.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import static com.caju.transactionauthorizer.service.impl.TestConstants.ACCOUNT;
import static com.caju.transactionauthorizer.service.impl.TestConstants.ACCOUNT_ID;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_0;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_100;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_200;
import static com.caju.transactionauthorizer.service.impl.TestConstants.AMOUNT_50;
import static com.caju.transactionauthorizer.service.impl.TestConstants.ID;
import static com.caju.transactionauthorizer.service.impl.TestConstants.MCC;
import static com.caju.transactionauthorizer.service.impl.TestConstants.MERCHANT;
import static com.caju.transactionauthorizer.service.impl.TestConstants.UNEXPECTED_ERROR_MSG;
import static com.caju.transactionauthorizer.service.impl.TestConstants.VERSION_1L;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TransactionServiceImpl}.
 *
 * <p>Covers the complete authorization flow including MCC resolution (L1/L3),
 * balance fallback (L2), optimistic locking failures (L4), and error paths.</p>
 */
@DisplayName("TransactionServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private MerchantCategoryCodesService categoryCodesService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient()
                .when(meterRegistry.counter(anyString())).thenReturn(counter);
        org.mockito.Mockito.lenient()
                .when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

        transactionService = new TransactionServiceImpl(
                transactionRepository, balanceService, merchantService, categoryCodesService, meterRegistry);
    }

    // ---- performTransaction ----

    @Test
    @DisplayName("performTransaction should return INSUFFICIENT_FUNDS when balance too low")
    void shouldReturnInsufficientFundsWhenBalanceTooLow() {
        TransactionModel model = transactionModel(MCC);
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_50);
        MerchantDocument merchant = merchantDocument(MCC);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchant));

        TransactionCodeModel result = transactionService.performTransaction(model);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    @DisplayName("performTransaction should return PROCESSING_ERROR when account is not found")
    void shouldReturnProcessingErrorWhenAccountNotFound() {
        TransactionModel model = transactionModel(MCC);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.empty());

        TransactionCodeModel result = transactionService.performTransaction(model);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    @Test
    @DisplayName("performTransaction should return APPROVED and debit balance when funds are sufficient")
    void shouldReturnApprovedWhenFundsAreSufficient() {
        TransactionModel model = transactionModel(MCC);
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_200);
        MerchantDocument merchant = merchantDocument(MCC);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchant));

        TransactionCodeModel result = transactionService.performTransaction(model);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    @DisplayName("performTransaction should return PROCESSING_ERROR on OptimisticLockingFailureException")
    void shouldReturnProcessingErrorOnOptimisticLockingFailure() {
        TransactionModel model = transactionModel(MCC);

        when(balanceService.findByAccount(ACCOUNT_ID))
                .thenThrow(new OptimisticLockingFailureException(UNEXPECTED_ERROR_MSG));

        TransactionCodeModel result = transactionService.performTransaction(model);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
        verify(balanceService, times(1)).findByAccount(ACCOUNT_ID);
    }

    @Test
    @DisplayName("performTransaction should return PROCESSING_ERROR on any generic exception")
    void shouldReturnProcessingErrorOnGenericException() {
        TransactionModel model = transactionModel(MCC);

        when(balanceService.findByAccount(ACCOUNT_ID)).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MSG));

        TransactionCodeModel result = transactionService.performTransaction(model);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    // ---- determineMccCategory (L3) ----

    @Test
    @DisplayName("determineMccCategory should use merchant override MCC when merchant name matches")
    void shouldUseMerchantOverrideMccWhenMerchantFound() {
        TransactionModel model = transactionModel(MCC);
        MerchantDocument merchant = merchantDocument(MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchant));

        String result = transactionService.determineMccCategory(model);

        assertEquals(MCC, result);
    }

    @Test
    @DisplayName("determineMccCategory should fall back to MCC code when no merchant override exists")
    void shouldFallBackToMccCodeWhenNoMerchantOverride() {
        TransactionModel model = transactionModel(MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryCodesService.findByCode(anyString()))
                .thenReturn(Optional.of(new MerchantCategoryCodesDocument(ID, MCC, CategoryCodeName.FOOD)));

        String result = transactionService.determineMccCategory(model);

        assertEquals(MCC, result);
    }

    // ---- updateWalletBalance (L1/L2) ----

    @Test
    @DisplayName("updateWalletBalance should return APPROVED for CASH category with sufficient balance")
    void shouldApproveWhenCashCategoryHasSufficientBalance() {
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_200);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    @DisplayName("updateWalletBalance should return INSUFFICIENT_FUNDS when CASH balance is too low")
    void shouldRejectWhenCashBalanceIsInsufficient() {
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_50);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    @DisplayName("updateWalletBalance should return APPROVED for FOOD category with sufficient balance")
    void shouldApproveWhenFoodCategoryHasSufficientBalance() {
        BalanceDocument balance = balanceDocument(AMOUNT_100, AMOUNT_0, AMOUNT_0);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.FOOD, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    @DisplayName("updateWalletBalance should return APPROVED for MEAL category with sufficient balance")
    void shouldApproveWhenMealCategoryHasSufficientBalance() {
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_100, AMOUNT_0);

        TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.MEAL, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    // ---- updateBalanceWithFallback (L2) ----

    @Test
    @DisplayName("updateBalanceWithFallback should debit primary bucket when it has sufficient funds")
    void shouldDebitPrimaryBucketWhenSufficient() {
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_200);

        boolean result = transactionService.updateBalanceWithFallback(
                balance::getCash, balance::setCash, balance::getFood, balance::setFood, AMOUNT_100);

        assertTrue(result);
        assertEquals(AMOUNT_100, balance.getCash());
    }

    @Test
    @DisplayName("updateBalanceWithFallback should debit fallback bucket when primary has insufficient funds")
    void shouldDebitFallbackBucketWhenPrimaryInsufficient() {
        BalanceDocument balance = balanceDocument(AMOUNT_0, AMOUNT_0, AMOUNT_100);

        boolean result = transactionService.updateBalanceWithFallback(
                balance::getFood, balance::setFood,
                balance::getCash, balance::setCash,
                AMOUNT_50);

        assertTrue(result);
        assertEquals(AMOUNT_50, balance.getCash());
        assertEquals(AMOUNT_0, balance.getFood());
    }

    // ---- saveTransaction ----

    @Test
    @DisplayName("saveTransaction should persist a transaction document")
    void shouldPersistTransactionDocument() {
        TransactionModel model = transactionModel(MCC);

        transactionService.saveTransaction(model, ACCOUNT_ID, AMOUNT_100, MCC);

        verify(transactionRepository, times(1)).save(any(TransactionDocument.class));
    }

    // ---- performTransactionFallback (Circuit Breaker) ----

    @Test
    @DisplayName("performTransactionFallback should return PROCESSING_ERROR when circuit is open")
    void shouldReturnProcessingErrorFromCircuitBreakerFallback() {
        TransactionModel model = transactionModel(MCC);
        RuntimeException cause = new RuntimeException("MongoDB unavailable");

        TransactionCodeModel result = transactionService.performTransactionFallback(model, cause);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    // ---- helpers ----

    private TransactionModel transactionModel(String mcc) {
        return new TransactionModel(ACCOUNT_ID, AMOUNT_100, mcc, MERCHANT);
    }

    private BalanceDocument balanceDocument(BigDecimal food, BigDecimal meal, BigDecimal cash) {
        return new BalanceDocument(ID, ACCOUNT, food, meal, cash, VERSION_1L);
    }

    private MerchantDocument merchantDocument(String mcc) {
        return new MerchantDocument(ID, MERCHANT, mcc);
    }
}
