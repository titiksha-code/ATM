package com.globalbank.atm.dto.response;

import com.globalbank.atm.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long txnId;
    private TransactionType txnType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String txnRef;
    private LocalDateTime txnDate;

    public TransactionResponse() {
    }

    public TransactionResponse(Long txnId, TransactionType txnType, BigDecimal amount,
                               BigDecimal balanceAfter, String description, String txnRef, LocalDateTime txnDate) {
        this.txnId = txnId;
        this.txnType = txnType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.txnRef = txnRef;
        this.txnDate = txnDate;
    }

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public TransactionType getTxnType() {
        return txnType;
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDateTime txnDate) {
        this.txnDate = txnDate;
    }
}
