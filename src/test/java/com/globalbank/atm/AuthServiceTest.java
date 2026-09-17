package com.globalbank.atm;

import com.globalbank.atm.dto.request.LoginRequest;
import com.globalbank.atm.dto.request.PinChangeRequest;
import com.globalbank.atm.dto.response.AuthResponse;
import com.globalbank.atm.entity.UserEntity;
import com.globalbank.atm.exception.AtmExceptions.*;
import com.globalbank.atm.repository.UserRepository;
import com.globalbank.atm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private static final String DEMO_CARD = "4001234567890004"; // Titiksha Gupta

    @BeforeEach
    void setup() {
        authService.unlockCard(DEMO_CARD);
    }

    @Test
    void testSuccessfulLogin() {
        LoginRequest req = new LoginRequest(DEMO_CARD, "2004");
        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertNotNull(res.getToken());
        assertEquals("Titiksha Gupta", res.getFullName());
        assertEquals("GB100000004", res.getAccountNo());
    }

    @Test
    void testWrongPinThrowsInvalidPinException() {
        LoginRequest req = new LoginRequest(DEMO_CARD, "0000");

        InvalidPinException ex = assertThrows(InvalidPinException.class, () -> authService.login(req));
        assertTrue(ex.getMessage().contains("attempt(s) remaining"));

        UserEntity user = userRepository.findByCardNumber(DEMO_CARD).orElseThrow();
        assertEquals(1, user.getFailedAttempts());
        assertFalse(user.isLocked());
    }

    @Test
    void testThreeWrongPinAttemptsLocksAccount() {
        LoginRequest req = new LoginRequest(DEMO_CARD, "0000");

        // Attempt 1
        assertThrows(InvalidPinException.class, () -> authService.login(req));
        // Attempt 2
        assertThrows(InvalidPinException.class, () -> authService.login(req));
        // Attempt 3 -> Should lock
        CardLockedException lockedEx = assertThrows(CardLockedException.class, () -> authService.login(req));
        assertTrue(lockedEx.getMessage().contains("locked"));

        UserEntity user = userRepository.findByCardNumber(DEMO_CARD).orElseThrow();
        assertTrue(user.isLocked());
        assertEquals(3, user.getFailedAttempts());

        // Admin unlocks card
        authService.unlockCard(DEMO_CARD);
        UserEntity unlockedUser = userRepository.findByCardNumber(DEMO_CARD).orElseThrow();
        assertFalse(unlockedUser.isLocked());
        assertEquals(0, unlockedUser.getFailedAttempts());
    }

    @Test
    void testChangePinSuccess() {
        PinChangeRequest req = new PinChangeRequest("2004", "4321", "4321");
        assertDoesNotThrow(() -> authService.changePin(DEMO_CARD, req));

        // Old PIN should no longer work
        assertThrows(InvalidPinException.class, () -> authService.login(new LoginRequest(DEMO_CARD, "2004")));

        // New PIN should work
        AuthResponse res = authService.login(new LoginRequest(DEMO_CARD, "4321"));
        assertNotNull(res.getToken());
    }

    @Test
    void testChangePinWithWrongCurrentPinFails() {
        PinChangeRequest req = new PinChangeRequest("9999", "4321", "4321");
        assertThrows(InvalidPinException.class, () -> authService.changePin(DEMO_CARD, req));
    }
}
