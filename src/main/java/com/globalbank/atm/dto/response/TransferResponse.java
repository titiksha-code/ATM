package com.globalbank.atm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {

    private boolean success;
    private String txnRef;
    private BigDecimal amount;
    private String senderAccountNo;
    private String recipientName;
    private String recipientAccountNo;
    private BigDecimal newBalance;
    private String message;
    private LocalDateTime timestamp;

    public TransferResponse() {
    }

    public TransferResponse(boolean success, String txnRef, BigDecimal amount, String senderAccountNo,
                            String recipientName, String recipientAccountNo, BigDecimal newBalance,
                            String message, LocalDateTime timestamp) {
        this.success = success;
        this.txnRef = txnRef;
        this.amount = amount;
        this.senderAccountNo = senderAccountNo;
        this.recipientName = recipientName;
        this.recipientAccountNo = recipientAccountNo;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSenderAccountNo() {
        return senderAccountNo;
    }

    public void setSenderAccountNo(String senderAccountNo) {
        this.senderAccountNo = senderAccountNo;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAccountNo() {
        return recipientAccountNo;
    }

    public void setRecipientAccountNo(String recipientAccountNo) {
        this.recipientAccountNo = recipientAccountNo;
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
