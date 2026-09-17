package com.globalbank.atm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositResponse {

    private boolean success;
    private String txnRef;
    private BigDecimal amountDeposited;
    private BigDecimal newBalance;
    private String message;
    private LocalDateTime timestamp;

    public DepositResponse() {
    }

    public DepositResponse(boolean success, String txnRef, BigDecimal amountDeposited,
                           BigDecimal newBalance, String message, LocalDateTime timestamp) {
        this.success = success;
        this.txnRef = txnRef;
        this.amountDeposited = amountDeposited;
        this.newBalance = newBalance;
        this.message = message;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public BigDecimal getAmountDeposited() {
        return amountDeposited;
    }

    public void setAmountDeposited(BigDecimal amountDeposited) {
        this.amountDeposited = amountDeposited;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
