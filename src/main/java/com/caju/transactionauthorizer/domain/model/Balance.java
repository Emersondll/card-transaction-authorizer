package com.caju.transactionauthorizer.domain.model;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class Balance {

    private final String id;
    private final String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;
    private final Long version;

    public Balance(String id, String account, BigDecimal food, BigDecimal meal, BigDecimal cash, Long version) {
        this.id = id;
        this.account = account;
        this.food = food;
        this.meal = meal;
        this.cash = cash;
        this.version = version;
    }

    public boolean deductFrom(CategoryCode category, BigDecimal amount) {
        return switch (category) {
            case FOOD -> tryDeduct(food, amount, v -> food = v);
            case MEAL -> tryDeduct(meal, amount, v -> meal = v);
            case CASH -> tryDeduct(cash, amount, v -> cash = v);
        };
    }

    public boolean deductCash(BigDecimal amount) {
        return tryDeduct(cash, amount, v -> cash = v);
    }

    private boolean tryDeduct(BigDecimal balance, BigDecimal amount, Consumer<BigDecimal> setter) {
        BigDecimal result = balance.subtract(amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        setter.accept(result);
        return true;
    }

    public String getId() { return id; }
    public String getAccount() { return account; }
    public BigDecimal getFood() { return food; }
    public BigDecimal getMeal() { return meal; }
    public BigDecimal getCash() { return cash; }
    public Long getVersion() { return version; }
}
