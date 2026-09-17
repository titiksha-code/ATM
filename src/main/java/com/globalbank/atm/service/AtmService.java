package com.globalbank.atm.service;

import com.globalbank.atm.dto.request.DepositRequest;
import com.globalbank.atm.dto.request.TransferRequest;
import com.globalbank.atm.dto.request.WithdrawRequest;
import com.globalbank.atm.dto.response.*;
import com.globalbank.atm.entity.*;
import com.globalbank.atm.exception.AtmExceptions.*;
import com.globalbank.atm.repository.AccountRepository;
import com.globalbank.atm.repository.TransactionRepository;
import com.globalbank.atm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AtmService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Value("${globalbank.atm.daily-withdrawal-limit:2000.00}")
    private BigDecimal dailyWithdrawalLimit;

    @Value("${globalbank.atm.max-single-deposit:50000.00}")
    private BigDecimal maxSingleDeposit;

    public AtmService(AccountRepository accountRepository,
                      UserRepository userRepository,
                      TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public AccountOverviewResponse getAccountOverview(String cardNumber) {
        AccountEntity account = getAccountByCardNumber(cardNumber);
        refreshDailyWithdrawal(account);

        BigDecimal remainingDaily = dailyWithdrawalLimit.subtract(account.getDailyWithdrawnToday()).max(BigDecimal.ZERO);

        return new AccountOverviewResponse(
                account.getUser().getFullName(),
                AuthService.maskCard(cardNumber),
                cardNumber,
                account.getAccountNo(),
                account.getAccountType().name(),
                account.getBalance(),
                account.getDailyWithdrawnToday(),
                remainingDaily
        );
    }

    @Transactional
    public WithdrawResponse withdraw(String cardNumber, WithdrawRequest request) {
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }

        // Amount must be in multiples of 10
        if (amount.remainder(BigDecimal.valueOf(10)).compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAmountException("Withdrawal amount must be in multiples of $10.");
        }

        AccountEntity account = getAccountByCardNumber(cardNumber);
        refreshDailyWithdrawal(account);

        // Check daily withdrawal limit
        BigDecimal projectedDaily = account.getDailyWithdrawnToday().add(amount);
        if (projectedDaily.compareTo(dailyWithdrawalLimit) > 0) {
            BigDecimal remainingLimit = dailyWithdrawalLimit.subtract(account.getDailyWithdrawnToday()).max(BigDecimal.ZERO);
            throw new DailyLimitExceededException(
                    "Daily withdrawal limit of $" + dailyWithdrawalLimit + " exceeded. You can withdraw up to $" +
                    remainingLimit + " more today."
            );
        }

        // Check balance
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available balance: $" + String.format("%,.2f", account.getBalance())
            );
        }

        // Execute withdrawal
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        account.setDailyWithdrawnToday(projectedDaily);
        account.setLastWithdrawalDate(LocalDate.now());
        accountRepository.save(account);

        String txnRef = generateTxnRef();
        TransactionEntity txn = new TransactionEntity(
                account,
                TransactionType.WITHDRAWAL,
                amount,
                newBalance,
                "ATM CASH WITHDRAWAL",
                txnRef
        );
        transactionRepository.save(txn);

        BigDecimal remainingLimit = dailyWithdrawalLimit.subtract(projectedDaily).max(BigDecimal.ZERO);

        return new WithdrawResponse(
                true,
                txnRef,
                amount,
                newBalance,
                projectedDaily,
                remainingLimit,
                "Please collect your cash: $" + String.format("%,.2f", amount),
                LocalDateTime.now()
        );
    }

    @Transactional
    public DepositResponse deposit(String cardNumber, DepositRequest request) {
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }

        if (amount.compareTo(maxSingleDeposit) > 0) {
            throw new InvalidAmountException("Maximum single deposit limit is $" + String.format("%,.2f", maxSingleDeposit));
        }

        AccountEntity account = getAccountByCardNumber(cardNumber);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        String txnRef = generateTxnRef();
        TransactionEntity txn = new TransactionEntity(
                account,
                TransactionType.DEPOSIT,
                amount,
                newBalance,
                "ATM CASH DEPOSIT",
                txnRef
        );
        transactionRepository.save(txn);

        return new DepositResponse(
                true,
                txnRef,
                amount,
                newBalance,
                "Successfully deposited $" + String.format("%,.2f", amount),
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public RecipientVerificationResponse verifyRecipient(String senderCardNumber, String target) {
        if (target == null || target.trim().isEmpty()) {
            return new RecipientVerificationResponse(false, null, null, null, "Recipient card or account number required");
        }

        target = target.trim();

        // Check if target is sender themselves
        if (target.equalsIgnoreCase(senderCardNumber)) {
            return new RecipientVerificationResponse(false, null, null, null, "Cannot transfer funds to your own card");
        }

        // Try lookup by card number first
        var userOpt = userRepository.findByCardNumber(target);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (!user.isActive()) {
                return new RecipientVerificationResponse(false, null, null, null, "Recipient card is inactive");
            }
            AccountEntity acc = accountRepository.findByUser_UserId(user.getUserId()).orElse(null);
            if (acc == null) {
                return new RecipientVerificationResponse(false, null, null, null, "Recipient has no active bank account");
            }
            if (acc.getAccountNo().equalsIgnoreCase(getAccountByCardNumber(senderCardNumber).getAccountNo())) {
                return new RecipientVerificationResponse(false, null, null, null, "Cannot transfer funds to your own account");
            }
            return new RecipientVerificationResponse(
                    true,
                    user.getFullName(),
                    acc.getAccountNo(),
                    AuthService.maskCard(user.getCardNumber()),
                    "Recipient verified: " + user.getFullName()
            );
        }

        // Try lookup by account number
        var accOpt = accountRepository.findByAccountNo(target);
        if (accOpt.isPresent()) {
            AccountEntity acc = accOpt.get();
            UserEntity user = acc.getUser();
            if (!user.isActive()) {
                return new RecipientVerificationResponse(false, null, null, null, "Recipient account is inactive");
            }
            if (user.getCardNumber().equalsIgnoreCase(senderCardNumber)) {
                return new RecipientVerificationResponse(false, null, null, null, "Cannot transfer funds to your own account");
            }
            return new RecipientVerificationResponse(
                    true,
                    user.getFullName(),
                    acc.getAccountNo(),
                    AuthService.maskCard(user.getCardNumber()),
                    "Recipient verified: " + user.getFullName()
            );
        }

        return new RecipientVerificationResponse(false, null, null, null, "Recipient account or card not found");
    }

    @Transactional
    public TransferResponse transfer(String senderCardNumber, TransferRequest request) {
        BigDecimal amount = request.getAmount();
        String target = request.getRecipientTarget().trim();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }

        AccountEntity senderAccount = getAccountByCardNumber(senderCardNumber);

        if (amount.compareTo(senderAccount.getBalance()) > 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds for transfer. Available balance: $" + String.format("%,.2f", senderAccount.getBalance())
            );
        }

        // Find recipient account
        AccountEntity recipientAccount = null;
        UserEntity recipientUser = null;

        var userOpt = userRepository.findByCardNumber(target);
        if (userOpt.isPresent()) {
            recipientUser = userOpt.get();
            recipientAccount = accountRepository.findByUser_UserId(recipientUser.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException("Recipient has no active bank account"));
        } else {
            var accOpt = accountRepository.findByAccountNo(target);
            if (accOpt.isPresent()) {
                recipientAccount = accOpt.get();
                recipientUser = recipientAccount.getUser();
            } else {
                throw new AccountNotFoundException("Recipient account or card number not found in system.");
            }
        }

        if (recipientAccount.getAccountId().equals(senderAccount.getAccountId())) {
            throw new SelfTransferException("Cannot transfer funds to your own account.");
        }

        if (!recipientUser.isActive()) {
            throw new CardBlockedException("Recipient account is deactivated.");
        }

        // Perform transfer atomically
        BigDecimal senderNewBalance = senderAccount.getBalance().subtract(amount);
        BigDecimal recipientNewBalance = recipientAccount.getBalance().add(amount);

        senderAccount.setBalance(senderNewBalance);
        recipientAccount.setBalance(recipientNewBalance);

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        String txnRef = generateTxnRef();

        // Record sender transaction (TRANSFER_OUT)
        TransactionEntity senderTxn = new TransactionEntity(
                senderAccount,
                TransactionType.TRANSFER_OUT,
                amount,
                senderNewBalance,
                "TRANSFER TO " + recipientAccount.getAccountNo() + " (" + recipientUser.getFullName() + ")",
                txnRef + "-OUT"
        );
        transactionRepository.save(senderTxn);

        // Record recipient transaction (TRANSFER_IN)
        TransactionEntity recipientTxn = new TransactionEntity(
                recipientAccount,
                TransactionType.TRANSFER_IN,
                amount,
                recipientNewBalance,
                "TRANSFER FROM " + senderAccount.getAccountNo() + " (" + senderAccount.getUser().getFullName() + ")",
                txnRef + "-IN"
        );
        transactionRepository.save(recipientTxn);

        return new TransferResponse(
                true,
                txnRef,
                amount,
                senderAccount.getAccountNo(),
                recipientUser.getFullName(),
                recipientAccount.getAccountNo(),
                senderNewBalance,
                "Successfully transferred $" + String.format("%,.2f", amount) + " to " + recipientUser.getFullName(),
                LocalDateTime.now()
        );
    }

    private AccountEntity getAccountByCardNumber(String cardNumber) {
        return accountRepository.findByUser_CardNumber(cardNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for card: " + cardNumber));
    }

    private void refreshDailyWithdrawal(AccountEntity account) {
        LocalDate today = LocalDate.now();
        if (account.getLastWithdrawalDate() == null || !account.getLastWithdrawalDate().equals(today)) {
            account.setDailyWithdrawnToday(BigDecimal.ZERO);
            account.setLastWithdrawalDate(today);
            accountRepository.save(account);
        }
    }

    public static String generateTxnRef() {
        return "TXN" + System.currentTimeMillis();
    }
}
