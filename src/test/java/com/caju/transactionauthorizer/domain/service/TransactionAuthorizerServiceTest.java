package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.*;
import com.caju.transactionauthorizer.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionAuthorizerServiceTest {

    @Mock BalanceRepositoryPort balancePort;
    @Mock MerchantRepositoryPort merchantPort;
    @Mock MerchantCategoryRepositoryPort categoryPort;
    @Mock TransactionRepositoryPort transactionPort;
    @Mock BalanceDeductionService deductionService;

    @InjectMocks TransactionAuthorizerService service;

    private static final String ACCOUNT_ID = "123";
    private static final BigDecimal AMOUNT = new BigDecimal("50.00");
    private static final String MCC = "5411";
    private static final String MERCHANT = "PADARIA DO ZE";

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void authorize_accountNotFound_returnsProcessingError() {
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertEquals(AuthorizationStatus.PROCESSING_ERROR,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
    }

    @Test
    void authorize_deductionSucceeds_returnsApproved() {
        Balance balance = new Balance("id", ACCOUNT_ID, new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode(MCC)).thenReturn(Optional.of(new MerchantCategory("id", MCC, CategoryCode.FOOD)));
        when(deductionService.deduct(balance, CategoryCode.FOOD, AMOUNT)).thenReturn(true);

        assertEquals(AuthorizationStatus.APPROVED, service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
        verify(balancePort).save(balance);
        verify(transactionPort).save(ACCOUNT_ID, AMOUNT, MERCHANT, MCC);
    }

    @Test
    void authorize_deductionFails_returnsInsufficientFunds() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode(MCC)).thenReturn(Optional.of(new MerchantCategory("id", MCC, CategoryCode.FOOD)));
        when(deductionService.deduct(balance, CategoryCode.FOOD, AMOUNT)).thenReturn(false);

        assertEquals(AuthorizationStatus.INSUFFICIENT_FUNDS,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
        verify(balancePort, never()).save(any());
    }

    @Test
    void authorize_merchantOverridePresent_usesMerchantMcc() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, new BigDecimal("100.00"), BigDecimal.ZERO, 1L);
        Merchant merchant = new Merchant("mid", MERCHANT, "5812");
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.of(merchant));
        when(categoryPort.findByCode("5812")).thenReturn(Optional.of(new MerchantCategory("id", "5812", CategoryCode.MEAL)));
        when(deductionService.deduct(balance, CategoryCode.MEAL, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.MEAL, AMOUNT);
    }

    @Test
    void authorize_nullMcc_usesCashCategory() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(deductionService.deduct(balance, CategoryCode.CASH, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, null, MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.CASH, AMOUNT);
    }

    @Test
    void authorize_unknownMcc_usesCashCategory() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode("9999")).thenReturn(Optional.empty());
        when(deductionService.deduct(balance, CategoryCode.CASH, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, "9999", MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.CASH, AMOUNT);
    }

    @Test
    void authorize_exceptionThrown_returnsProcessingError() {
        when(balancePort.findByAccount(any())).thenThrow(new RuntimeException("DB error"));
        assertEquals(AuthorizationStatus.PROCESSING_ERROR,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
    }
}
