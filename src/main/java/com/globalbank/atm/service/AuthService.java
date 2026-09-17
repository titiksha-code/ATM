package com.globalbank.atm.service;

import com.globalbank.atm.dto.request.LoginRequest;
import com.globalbank.atm.dto.request.PinChangeRequest;
import com.globalbank.atm.dto.response.AuthResponse;
import com.globalbank.atm.dto.response.DemoCardDto;
import com.globalbank.atm.entity.AccountEntity;
import com.globalbank.atm.entity.AtmSessionEntity;
import com.globalbank.atm.entity.SessionStatus;
import com.globalbank.atm.entity.UserEntity;
import com.globalbank.atm.exception.AtmExceptions.*;
import com.globalbank.atm.repository.AccountRepository;
import com.globalbank.atm.repository.AtmSessionRepository;
import com.globalbank.atm.repository.UserRepository;
import com.globalbank.atm.security.HybridPasswordEncoder;
import com.globalbank.atm.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AtmSessionRepository sessionRepository;
    private final HybridPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${globalbank.atm.max-failed-attempts:3}")
    private int maxFailedAttempts;

    public AuthService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       AtmSessionRepository sessionRepository,
                       HybridPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(noRollbackFor = {InvalidPinException.class, CardLockedException.class})
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new CardNotFoundException("Card number not recognized in GlobalBank network"));

        if (!user.isActive()) {
            throw new CardBlockedException("This card has been deactivated. Please contact your branch.");
        }

        if (user.isLocked()) {
            throw new CardLockedException("Card is currently locked due to previous failed attempts. Please contact bank administration.");
        }

        boolean matches = passwordEncoder.matches(request.getPin(), user.getPinHash());

        if (!matches) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= maxFailedAttempts) {
                user.setLocked(true);
                user.setLockedAt(LocalDateTime.now());
                userRepository.save(user);

                sessionRepository.save(new AtmSessionEntity(request.getCardNumber(), SessionStatus.LOCKED));
                throw new CardLockedException("Card has been locked after " + maxFailedAttempts + " failed attempts. Please contact bank administration to unlock.");
            } else {
                userRepository.save(user);
                sessionRepository.save(new AtmSessionEntity(request.getCardNumber(), SessionStatus.FAILED));
                int remaining = maxFailedAttempts - attempts;
                throw new InvalidPinException("Invalid PIN. " + remaining + " attempt(s) remaining before card lockout.");
            }
        }

        // Reset failed attempts on successful login
        user.setFailedAttempts(0);
        userRepository.save(user);

        // Record active session
        sessionRepository.save(new AtmSessionEntity(request.getCardNumber(), SessionStatus.ACTIVE));

        AccountEntity account = accountRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new AccountNotFoundException("No active bank account linked to this card"));

        String token = jwtService.generateToken(
                user.getCardNumber(),
                user.getUserId(),
                user.getFullName(),
                account.getAccountNo(),
                user.getRole()
        );

        String maskedCard = maskCard(user.getCardNumber());

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getFullName(),
                maskedCard,
                account.getAccountNo(),
                account.getAccountType().name(),
                account.getBalance()
        );
    }

    @Transactional
    public void logout(String cardNumber) {
        sessionRepository.findFirstByCardNumberAndStatusOrderByLoginTimeDesc(cardNumber, SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    session.setStatus(SessionStatus.CLOSED);
                    session.setLogoutTime(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }

    @Transactional
    public void changePin(String cardNumber, PinChangeRequest request) {
        UserEntity user = userRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPin(), user.getPinHash())) {
            throw new InvalidPinException("Current PIN is incorrect.");
        }

        if (!request.getNewPin().equals(request.getConfirmPin())) {
            throw new IllegalArgumentException("New PIN and Confirm PIN do not match.");
        }

        if (request.getCurrentPin().equals(request.getNewPin())) {
            throw new IllegalArgumentException("New PIN cannot be the same as the current PIN.");
        }

        user.setPinHash(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);
    }

    @Transactional
    public void unlockCard(String cardNumber) {
        UserEntity user = userRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found: " + cardNumber));

        user.setLocked(false);
        user.setFailedAttempts(0);
        user.setLockedAt(null);
        userRepository.save(user);
    }

    public List<DemoCardDto> getDemoCards() {
        return userRepository.findAll().stream().map(user -> {
            AccountEntity acc = accountRepository.findByUser_UserId(user.getUserId()).orElse(null);
            String pinHint = getPinHintForDemo(user.getCardNumber());
            return new DemoCardDto(
                    user.getCardNumber(),
                    maskCard(user.getCardNumber()),
                    user.getFullName(),
                    pinHint,
                    acc != null ? acc.getAccountNo() : "N/A",
                    acc != null ? acc.getAccountType().name() : "SAVINGS",
                    acc != null ? acc.getBalance() : null
            );
        }).collect(Collectors.toList());
    }

    private String getPinHintForDemo(String cardNumber) {
        if (cardNumber.endsWith("0001")) return "1234";
        if (cardNumber.endsWith("0002")) return "5678";
        if (cardNumber.endsWith("0003")) return "9999";
        if (cardNumber.endsWith("0004")) return "2004";
        return "****";
    }

    public static String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
