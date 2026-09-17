package com.globalbank.atm.controller;

import com.globalbank.atm.dto.request.DepositRequest;
import com.globalbank.atm.dto.request.TransferRequest;
import com.globalbank.atm.dto.request.WithdrawRequest;
import com.globalbank.atm.dto.response.*;
import com.globalbank.atm.service.AtmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/atm")
@Tag(name = "2. ATM Operations", description = "Balance enquiry, cash withdrawal, deposit, and inter-account transfer")
@SecurityRequirement(name = "bearerAuth")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @GetMapping("/balance")
    @Operation(summary = "Check Account Balance", description = "Fetches current account balance, account type, and daily withdrawal limit remaining")
    public ResponseEntity<ApiResponse<AccountOverviewResponse>> getBalance(Authentication authentication) {
        String cardNumber = authentication.getName();
        AccountOverviewResponse overview = atmService.getAccountOverview(cardNumber);
        return ResponseEntity.ok(ApiResponse.ok("Balance retrieved successfully", overview));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw Cash", description = "Withdraws cash in multiples of $10. Validates balance and enforces daily limit.")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdraw(
            Authentication authentication,
            @Valid @RequestBody WithdrawRequest request) {
        String cardNumber = authentication.getName();
        WithdrawResponse response = atmService.withdraw(cardNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit Funds", description = "Deposits cash into the authenticated account. Maximum single deposit $50,000.")
    public ResponseEntity<ApiResponse<DepositResponse>> deposit(
            Authentication authentication,
            @Valid @RequestBody DepositRequest request) {
        String cardNumber = authentication.getName();
        DepositResponse response = atmService.deposit(cardNumber, request);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }

    @GetMapping("/transfer/verify")
    @Operation(summary = "Verify Transfer Recipient", description = "Checks if target card or account exists and returns recipient name before transferring")
    public ResponseEntity<ApiResponse<RecipientVerificationResponse>> verifyRecipient(
            Authentication authentication,
            @RequestParam("target") String target) {
        String senderCard = authentication.getName();
        RecipientVerificationResponse response = atmService.verifyRecipient(senderCard, target);
        return ResponseEntity.ok(ApiResponse.ok("Verification completed", response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer Funds", description = "Performs atomic inter-account transfer with rollback protection on failure")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            Authentication authentication,
            @Valid @RequestBody TransferRequest request) {
        String senderCard = authentication.getName();
        TransferResponse response = atmService.transfer(senderCard, request);
        return ResponseEntity.ok(ApiResponse.ok(response.getMessage(), response));
    }
}
