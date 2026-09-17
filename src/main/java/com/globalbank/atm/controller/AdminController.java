package com.globalbank.atm.controller;

import com.globalbank.atm.dto.response.ApiResponse;
import com.globalbank.atm.entity.SessionStatus;
import com.globalbank.atm.repository.AccountRepository;
import com.globalbank.atm.repository.AtmSessionRepository;
import com.globalbank.atm.repository.TransactionRepository;
import com.globalbank.atm.repository.UserRepository;
import com.globalbank.atm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "4. Bank Administration", description = "Card unlock and ATM system metrics")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AtmSessionRepository sessionRepository;

    public AdminController(AuthService authService,
                           UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           AtmSessionRepository sessionRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/cards/{cardNumber}/unlock")
    @Operation(summary = "Unlock Card", description = "Resets failed PIN counter and unlocks a locked card")
    public ResponseEntity<ApiResponse<Void>> unlockCard(@PathVariable("cardNumber") String cardNumber) {
        authService.unlockCard(cardNumber);
        return ResponseEntity.ok(ApiResponse.ok("Card " + AuthService.maskCard(cardNumber) + " has been successfully unlocked"));
    }

    @GetMapping("/overview")
    @Operation(summary = "System Overview & Audit Stats", description = "Aggregates overall accounts, cash balances, and session activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        long userCount = userRepository.count();
        long totalTxns = transactionRepository.count();
        long activeSessions = sessionRepository.countByStatus(SessionStatus.ACTIVE);
        long lockedSessions = sessionRepository.countByStatus(SessionStatus.LOCKED);

        BigDecimal totalCash = accountRepository.findAll().stream()
                .map(acc -> acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalUsers", userCount);
        stats.put("totalTransactions", totalTxns);
        stats.put("totalDepositsInVault", totalCash);
        stats.put("activeSessions", activeSessions);
        stats.put("lockedCards", lockedSessions);
        stats.put("recentSessions", sessionRepository.findTop20ByOrderByLoginTimeDesc());

        return ResponseEntity.ok(ApiResponse.ok("System overview retrieved", stats));
    }
}
