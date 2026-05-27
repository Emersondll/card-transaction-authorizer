package com.caju.transactionauthorizer.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransactionRequest(
        @JsonProperty("account") @NotBlank String account,
        @JsonProperty("totalAmount") @NotNull @Positive BigDecimal totalAmount,
        @JsonProperty("mcc") String mcc,
        @JsonProperty("merchant") @NotBlank String merchant
) {}
