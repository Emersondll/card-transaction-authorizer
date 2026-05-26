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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static com.caju.transactionauthorizer.service.impl.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private MerchantCategoryCodesService categoryCodesService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformTransactionInsufficientFunds() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT, AMOUNT_0, AMOUNT_0, AMOUNT_50, VERSION_1L);
        final MerchantDocument merchantDocument = new MerchantDocument(ID, MERCHANT, MERCHANT);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchantDocument));

        final TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    void testPerformTransactionProcessingError() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.empty());

        final TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    @Test
    void testPerformTransactionSuccess() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);
        final MerchantDocument merchantDocument = new MerchantDocument(ID, MERCHANT, MERCHANT);

        when(balanceService.findByAccount(anyString())).thenReturn(Optional.of(balance));
        when(categoryCodesService.checkCategory(anyString())).thenReturn(CategoryCodeName.CASH);
        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchantDocument));

        final TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testDetermineMccCategory() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);
        final MerchantDocument merchant = new MerchantDocument(ID, MERCHANT, MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.of(merchant));

        final String result = transactionService.determineMccCategory(transactionModel);

        assertEquals(MCC, result);
    }

    @Test
    void testUpdateWalletBalanceSuccess() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);

        final TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceInsufficientFunds() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_50, VERSION_1L);

        final TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.CASH, AMOUNT_100);

        assertEquals(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode(), result.code());
    }

    @Test
    void testUpdateBalanceWithFallback() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_200, VERSION_1L);

        final boolean result = transactionService.updateBalanceWithFallback(balance::getCash, balance::setCash, balance::getFood, balance::setFood, AMOUNT_100);

        assertTrue(result);
        assertEquals(AMOUNT_100, balance.getCash());
    }

    @Test
    void testSaveTransaction() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        transactionService.saveTransaction(transactionModel, ACCOUNT_ID, AMOUNT_100, MCC);

        verify(transactionRepository, times(1)).save(any(TransactionDocument.class));
    }

    @Test
    void testPerformTransactionOptimisticLockingFailure() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(ACCOUNT_ID))
                .thenThrow(new OptimisticLockingFailureException(UNEXPECTED_ERROR_MSG));

        final TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
        verify(balanceService, times(1)).findByAccount(ACCOUNT_ID);
    }


    @Test
    void testPerformTransactionGenericException() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(balanceService.findByAccount(ACCOUNT_ID)).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MSG));

        final TransactionCodeModel result = transactionService.performTransaction(transactionModel);

        assertEquals(TransactionStatusCode.PROCESSING_ERROR.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceFoodCategory() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_100, AMOUNT_0, AMOUNT_0, VERSION_1L);

        final TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.FOOD, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testUpdateWalletBalanceMealCategory() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_100, AMOUNT_0, VERSION_1L);

        final TransactionCodeModel result = transactionService.updateWalletBalance(balance, CategoryCodeName.MEAL, AMOUNT_50);

        assertEquals(TransactionStatusCode.APPROVED.getCode(), result.code());
    }

    @Test
    void testDetermineMccCategoryFromCategoryCodes() {
        final TransactionModel transactionModel = new TransactionModel(ACCOUNT_ID, AMOUNT_100, MERCHANT, MCC);

        when(merchantService.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryCodesService.findByCode(anyString())).thenReturn(Optional.of(new MerchantCategoryCodesDocument(ID, MCC, CategoryCodeName.FOOD)));

        final String result = transactionService.determineMccCategory(transactionModel);

        assertEquals(MCC, result);
    }

    @Test
    void testUpdateBalanceWithFallbackWithNonNullFallback() {
        final BalanceDocument balance = new BalanceDocument(ID, ACCOUNT_ID, AMOUNT_0, AMOUNT_0, AMOUNT_100, 1L);

        final boolean result = transactionService.updateBalanceWithFallback(
                balance::getFood,
                balance::setFood,
                balance::getCash,
                balance::setCash,
                AMOUNT_50
        );

        assertTrue(result);
        assertEquals(AMOUNT_50, balance.getCash());
        assertEquals(AMOUNT_0, balance.getFood());
    }


}
