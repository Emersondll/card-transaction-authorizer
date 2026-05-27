package com.caju.transactionauthorizer.domain.port.in;

import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import java.math.BigDecimal;

public interface AuthorizeTransactionPort {
    AuthorizationStatus authorize(String accountId, BigDecimal amount, String mcc, String merchant);
}
