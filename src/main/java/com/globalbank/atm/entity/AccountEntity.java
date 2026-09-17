package com.globalbank.atm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "account_no", unique = true, nullable = false, length = 20)
    private String accountNo;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType = AccountType.SAVINGS;

    @Column(name = "daily_withdrawn_today", precision = 15, scale = 2)
    private BigDecimal dailyWithdrawnToday = BigDecimal.ZERO;

    @Column(name = "last_withdrawal_date")
    private LocalDate lastWithdrawalDate;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("txnDate DESC")
    private List<TransactionEntity> transactions = new ArrayList<>();

    public AccountEntity() {
    }

    public AccountEntity(UserEntity user, String accountNo, BigDecimal balance, AccountType accountType) {
        this.user = user;
        this.accountNo = accountNo;
        this.balance = balance;
        this.accountType = accountType;
        this.dailyWithdrawnToday = BigDecimal.ZERO;
        this.lastWithdrawalDate = LocalDate.now();
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getDailyWithdrawnToday() {
        return dailyWithdrawnToday != null ? dailyWithdrawnToday : BigDecimal.ZERO;
    }

    public void setDailyWithdrawnToday(BigDecimal dailyWithdrawnToday) {
        this.dailyWithdrawnToday = dailyWithdrawnToday;
    }

    public LocalDate getLastWithdrawalDate() {
        return lastWithdrawalDate;
    }

    public void setLastWithdrawalDate(LocalDate lastWithdrawalDate) {
        this.lastWithdrawalDate = lastWithdrawalDate;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(TransactionEntity transaction) {
        transactions.add(transaction);
        transaction.setAccount(this);
    }
}
