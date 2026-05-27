package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BalanceDeductionService {

    public boolean deduct(Balance balance, CategoryCode category, BigDecimal amount) {
        if (balance.deductFrom(category, amount)) {
            return true;
        }
        if (category != CategoryCode.CASH) {
            return balance.deductCash(amount);
        }
        return false;
    }
}
