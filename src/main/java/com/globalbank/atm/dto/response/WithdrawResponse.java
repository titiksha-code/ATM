package com.globalbank.atm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawResponse {

    private boolean success;
    private String txnRef;
    private BigDecimal amountWithdrawn;
    private BigDecimal newBalance;
    private BigDecimal dailyWithdrawnToday;
    private BigDecimal dailyLimitRemaining;
    private String message;
    private LocalDateTime timestamp;

    public WithdrawResponse() {
    }

    public WithdrawResponse(boolean success, String txnRef, BigDecimal amountWithdrawn,
                            BigDecimal newBalance, BigDecimal dailyWithdrawnToday,
                            BigDecimal dailyLimitRemaining, String message, LocalDateTime timestamp) {
        this.success = success;
        this.txnRef = txnRef;
        this.amountWithdrawn = amountWithdrawn;
        this.newBalance = newBalance;
        this.dailyWithdrawnToday = dailyWithdrawnToday;
        this.dailyLimitRemaining = dailyLimitRemaining;
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

    public BigDecimal getAmountWithdrawn() {
        return amountWithdrawn;
    }

    public void setAmountWithdrawn(BigDecimal amountWithdrawn) {
        this.amountWithdrawn = amountWithdrawn;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public BigDecimal getDailyWithdrawnToday() {
        return dailyWithdrawnToday;
    }

    public void setDailyWithdrawnToday(BigDecimal dailyWithdrawnToday) {
        this.dailyWithdrawnToday = dailyWithdrawnToday;
    }

    public BigDecimal getDailyLimitRemaining() {
        return dailyLimitRemaining;
    }

    public void setDailyLimitRemaining(BigDecimal dailyLimitRemaining) {
        this.dailyLimitRemaining = dailyLimitRemaining;
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
