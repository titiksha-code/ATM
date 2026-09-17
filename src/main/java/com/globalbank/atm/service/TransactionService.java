package com.globalbank.atm.service;

import com.globalbank.atm.dto.response.TransactionResponse;
import com.globalbank.atm.entity.AccountEntity;
import com.globalbank.atm.entity.TransactionEntity;
import com.globalbank.atm.entity.TransactionType;
import com.globalbank.atm.exception.AtmExceptions.AccountNotFoundException;
import com.globalbank.atm.repository.AccountRepository;
import com.globalbank.atm.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getMiniStatement(String cardNumber) {
        AccountEntity account = getAccount(cardNumber);
        List<TransactionEntity> list = transactionRepository.findTop8ByAccount_AccountIdOrderByTxnDateDesc(account.getAccountId());
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getFilteredTransactions(
            String cardNumber,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {

        AccountEntity account = getAccount(cardNumber);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(100, Math.max(1, size)));

        Page<TransactionEntity> entities = transactionRepository.findTransactionsFiltered(
                account.getAccountId(),
                type,
                startDate,
                endDate,
                pageable
        );

        return entities.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public byte[] exportStatementCsv(String cardNumber) {
        AccountEntity account = getAccount(cardNumber);
        List<TransactionEntity> list = transactionRepository.findAllByAccountIdOrderByTxnDateDesc(account.getAccountId());

        StringBuilder csv = new StringBuilder();
        csv.append("GlobalBank ATM - Account Statement\n");
        csv.append("Card Number:,").append(AuthService.maskCard(cardNumber)).append("\n");
        csv.append("Account Number:,").append(account.getAccountNo()).append("\n");
        csv.append("Holder Name:,").append(account.getUser().getFullName()).append("\n");
        csv.append("Current Balance:,$").append(account.getBalance()).append("\n");
        csv.append("Exported At:,").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");

        csv.append("Transaction ID,Date & Time,Type,Reference,Description,Amount ($),Balance After ($)\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TransactionEntity t : list) {
            csv.append(t.getTxnId()).append(",")
                    .append(t.getTxnDate().format(dtf)).append(",")
                    .append(t.getTxnType()).append(",")
                    .append(t.getTxnRef()).append(",")
                    .append("\"").append(t.getDescription() != null ? t.getDescription().replace("\"", "\"\"") : "").append("\",")
                    .append(t.getAmount()).append(",")
                    .append(t.getBalanceAfter()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private AccountEntity getAccount(String cardNumber) {
        return accountRepository.findByUser_CardNumber(cardNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for card: " + cardNumber));
    }

    private TransactionResponse mapToResponse(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getTxnId(),
                entity.getTxnType(),
                entity.getAmount(),
                entity.getBalanceAfter(),
                entity.getDescription(),
                entity.getTxnRef(),
                entity.getTxnDate()
        );
    }
}
