package com.globalbank.atm.dto.response;

import java.math.BigDecimal;

public class DemoCardDto {

    private String cardNumber;
    private String maskedCardNumber;
    private String name;
    private String pinHint;
    private String accountNo;
    private String accountType;
    private BigDecimal balance;

    public DemoCardDto() {
    }

    public DemoCardDto(String cardNumber, String maskedCardNumber, String name,
                       String pinHint, String accountNo, String accountType, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.maskedCardNumber = maskedCardNumber;
        this.name = name;
        this.pinHint = pinHint;
        this.accountNo = accountNo;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinHint() {
        return pinHint;
    }

    public void setPinHint(String pinHint) {
        this.pinHint = pinHint;
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
}
