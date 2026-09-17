package com.globalbank.atm.controller;

import com.globalbank.atm.dto.response.ApiResponse;
import com.globalbank.atm.dto.response.TransactionResponse;
import com.globalbank.atm.entity.TransactionType;
import com.globalbank.atm.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "3. Transactions & Statements", description = "Mini statement, filtered paginated history, and CSV statement export")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/mini-statement")
    @Operation(summary = "Get Mini Statement", description = "Fetches the last 8 transactions for the authenticated account")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getMiniStatement(Authentication authentication) {
        String cardNumber = authentication.getName();
        List<TransactionResponse> statement = transactionService.getMiniStatement(cardNumber);
        return ResponseEntity.ok(ApiResponse.ok("Mini statement retrieved", statement));
    }

    @GetMapping
    @Operation(summary = "Get Transaction History", description = "Fetches paginated transactions with optional filters for type and date range")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            Authentication authentication,
            @RequestParam(value = "type", required = false) TransactionType type,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        String cardNumber = authentication.getName();
        Page<TransactionResponse> result = transactionService.getFilteredTransactions(
                cardNumber, type, startDate, endDate, page, size
        );
        return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved", result));
    }

    @GetMapping("/export")
    @Operation(summary = "Export Account Statement CSV", description = "Generates and downloads a complete CSV account statement")
    public ResponseEntity<byte[]> exportStatement(Authentication authentication) {
        String cardNumber = authentication.getName();
        byte[] csvData = transactionService.exportStatementCsv(cardNumber);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-" + cardNumber + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
