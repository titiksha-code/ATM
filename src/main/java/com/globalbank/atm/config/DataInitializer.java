package com.globalbank.atm.config;

import com.globalbank.atm.entity.*;
import com.globalbank.atm.repository.AccountRepository;
import com.globalbank.atm.repository.TransactionRepository;
import com.globalbank.atm.repository.UserRepository;
import com.globalbank.atm.security.HybridPasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DataInitializer(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already exists
        }

        System.out.println(">>> Initializing GlobalBank ATM Demo Accounts and Data...");

        // 1. James Wilson
        createDemoUser("4001234567890001", "James Wilson", "1234",
                "GB100000001", new BigDecimal("18540.00"), AccountType.SAVINGS,
                "TXN000000001", "SALARY CREDIT", new BigDecimal("3200.00"));

        // 2. Sarah Mitchell
        createDemoUser("4001234567890002", "Sarah Mitchell", "5678",
                "GB100000002", new BigDecimal("52730.50"), AccountType.SAVINGS,
                "TXN000000003", "DIVIDEND CREDIT", new BigDecimal("1450.00"));

        // 3. Raj Patel
        createDemoUser("4001234567890003", "Raj Patel", "9999",
                "GB100000003", new BigDecimal("8200.00"), AccountType.CURRENT,
                "TXN000000004", "INITIAL DEPOSIT", new BigDecimal("8200.00"));

        // 4. Titiksha Gupta
        createDemoUser("4001234567890004", "Titiksha Gupta", "2004",
                "GB100000004", new BigDecimal("5000.00"), AccountType.SAVINGS,
                "TXN000000005", "ACCOUNT OPENING BONUS", new BigDecimal("5000.00"));

        System.out.println(">>> GlobalBank Demo Accounts Initialized Successfully!");
    }

    private void createDemoUser(String cardNumber, String name, String rawPin,
                                String accountNo, BigDecimal balance, AccountType type,
                                String seedRef, String seedDesc, BigDecimal seedAmount) {
        String pinHash = HybridPasswordEncoder.sha256(rawPin);
        UserEntity user = new UserEntity(cardNumber, name, pinHash);
        user = userRepository.save(user);

        AccountEntity account = new AccountEntity(user, accountNo, balance, type);
        account = accountRepository.save(account);

        TransactionEntity txn = new TransactionEntity(
                account,
                TransactionType.DEPOSIT,
                seedAmount,
                balance,
                seedDesc,
                seedRef
        );
        txn.setTxnDate(LocalDateTime.now().minusDays(1));
        transactionRepository.save(txn);
    }
}
