package com.globalbank.atm.dto.response;

import java.math.BigDecimal;

public class AccountOverviewResponse {

    private String fullName;
    private String maskedCardNumber;
    private String rawCardNumber;
    private String accountNo;
    private String accountType;
    private BigDecimal balance;
    private BigDecimal dailyWithdrawnToday;
    private BigDecimal dailyWithdrawalLimitRemaining;

    public AccountOverviewResponse() {
    }

    public AccountOverviewResponse(String fullName, String maskedCardNumber, String rawCardNumber,
                                   String accountNo, String accountType, BigDecimal balance,
                                   BigDecimal dailyWithdrawnToday, BigDecimal dailyWithdrawalLimitRemaining) {
        this.fullName = fullName;
        this.maskedCardNumber = maskedCardNumber;
        this.rawCardNumber = rawCardNumber;
        this.accountNo = accountNo;
        this.accountType = accountType;
        this.balance = balance;
        this.dailyWithdrawnToday = dailyWithdrawnToday;
        this.dailyWithdrawalLimitRemaining = dailyWithdrawalLimitRemaining;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getRawCardNumber() {
        return rawCardNumber;
    }

    public void setRawCardNumber(String rawCardNumber) {
        this.rawCardNumber = rawCardNumber;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getDailyWithdrawnToday() {
        return dailyWithdrawnToday;
    }

    public void setDailyWithdrawnToday(BigDecimal dailyWithdrawnToday) {
        this.dailyWithdrawnToday = dailyWithdrawnToday;
    }

    public BigDecimal getDailyWithdrawalLimitRemaining() {
        return dailyWithdrawalLimitRemaining;
    }

    public void setDailyWithdrawalLimitRemaining(BigDecimal dailyWithdrawalLimitRemaining) {
        this.dailyWithdrawalLimitRemaining = dailyWithdrawalLimitRemaining;
    }
}
