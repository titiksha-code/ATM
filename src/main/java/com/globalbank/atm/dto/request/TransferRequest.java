package com.globalbank.atm.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {

    @NotBlank(message = "Recipient card or account number is required")
    private String recipientTarget;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transfer amount is $1.00")
    private BigDecimal amount;

    public TransferRequest() {
    }

    public TransferRequest(String recipientTarget, BigDecimal amount) {
        this.recipientTarget = recipientTarget;
        this.amount = amount;
    }

    public String getRecipientTarget() {
        return recipientTarget;
    }

    public void setRecipientTarget(String recipientTarget) {
        this.recipientTarget = recipientTarget;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
