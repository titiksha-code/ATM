package com.globalbank.atm.controller;

import com.globalbank.atm.dto.request.LoginRequest;
import com.globalbank.atm.dto.request.PinChangeRequest;
import com.globalbank.atm.dto.response.ApiResponse;
import com.globalbank.atm.dto.response.AuthResponse;
import com.globalbank.atm.dto.response.DemoCardDto;
import com.globalbank.atm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Authentication", description = "Card login, logout, PIN change, and demo cards")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate ATM Card", description = "Validates 16-digit card number and 4-digit PIN. Locks card on 3 failed attempts.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Authentication successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout ATM Session", description = "Closes active ATM session and logs exit timestamp")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        String cardNumber = authentication.getName();
        authService.logout(cardNumber);
        return ResponseEntity.ok(ApiResponse.ok("Session closed successfully"));
    }

    @PostMapping("/change-pin")
    @Operation(summary = "Change ATM PIN", description = "Allows authenticated card holder to update their 4-digit PIN")
    public ResponseEntity<ApiResponse<Void>> changePin(
            Authentication authentication,
            @Valid @RequestBody PinChangeRequest request) {
        String cardNumber = authentication.getName();
        authService.changePin(cardNumber, request);
        return ResponseEntity.ok(ApiResponse.ok("PIN changed successfully"));
    }

    @GetMapping("/demo-cards")
    @Operation(summary = "List Demo Accounts", description = "Returns seeded demo cards with PIN hints for testing")
    public ResponseEntity<ApiResponse<List<DemoCardDto>>> getDemoCards() {
        List<DemoCardDto> list = authService.getDemoCards();
        return ResponseEntity.ok(ApiResponse.ok("Demo cards fetched", list));
    }
}
