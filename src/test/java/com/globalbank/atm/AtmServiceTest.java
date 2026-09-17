package com.globalbank.atm;

import com.globalbank.atm.dto.request.DepositRequest;
import com.globalbank.atm.dto.request.TransferRequest;
import com.globalbank.atm.dto.request.WithdrawRequest;
import com.globalbank.atm.dto.response.AccountOverviewResponse;
import com.globalbank.atm.dto.response.DepositResponse;
import com.globalbank.atm.dto.response.TransferResponse;
import com.globalbank.atm.dto.response.WithdrawResponse;
import com.globalbank.atm.exception.AtmExceptions.*;
import com.globalbank.atm.service.AtmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AtmServiceTest {

    @Autowired
    private AtmService atmService;

    private static final String TITIKSHA_CARD = "4001234567890004";
    private static final String JAMES_CARD     = "4001234567890001";

    @Test
    void testGetBalance() {
        AccountOverviewResponse overview = atmService.getAccountOverview(TITIKSHA_CARD);
        assertNotNull(overview);
        assertEquals("Titiksha Gupta", overview.getFullName());
        assertTrue(overview.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testDepositIncreasesBalance() {
        AccountOverviewResponse before = atmService.getAccountOverview(TITIKSHA_CARD);
        BigDecimal depositAmount = new BigDecimal("500.00");

        DepositResponse res = atmService.deposit(TITIKSHA_CARD, new DepositRequest(depositAmount));
        assertTrue(res.isSuccess());
        assertEquals(before.getBalance().add(depositAmount), res.getNewBalance());
    }

    @Test
    void testWithdrawDecreasesBalance() {
        AccountOverviewResponse before = atmService.getAccountOverview(TITIKSHA_CARD);
        BigDecimal withdrawAmount = new BigDecimal("100.00");

        WithdrawResponse res = atmService.withdraw(TITIKSHA_CARD, new WithdrawRequest(withdrawAmount));
        assertTrue(res.isSuccess());
        assertEquals(before.getBalance().subtract(withdrawAmount), res.getNewBalance());
    }

    @Autowired
    private com.globalbank.atm.repository.AccountRepository accountRepository;

    @Test
    void testWithdrawNonMultipleOfTenFails() {
        assertThrows(InvalidAmountException.class, () ->
                atmService.withdraw(TITIKSHA_CARD, new WithdrawRequest(new BigDecimal("15.00"))));
    }

    @Test
    void testWithdrawExceedingBalanceFails() {
        com.globalbank.atm.entity.AccountEntity account = accountRepository.findByUser_CardNumber(TITIKSHA_CARD).orElseThrow();
        account.setBalance(new BigDecimal("50.00"));
        accountRepository.save(account);

        assertThrows(InsufficientFundsException.class, () ->
                atmService.withdraw(TITIKSHA_CARD, new WithdrawRequest(new BigDecimal("100.00"))));
    }

    @Test
    void testWithdrawExceedingDailyLimitFails() {
        assertThrows(DailyLimitExceededException.class, () ->
                atmService.withdraw(TITIKSHA_CARD, new WithdrawRequest(new BigDecimal("2100.00"))));
    }

    @Test
    void testInterAccountTransfer() {
        AccountOverviewResponse senderBefore = atmService.getAccountOverview(TITIKSHA_CARD);
        AccountOverviewResponse recipientBefore = atmService.getAccountOverview(JAMES_CARD);

        BigDecimal transferAmount = new BigDecimal("200.00");
        TransferRequest req = new TransferRequest(JAMES_CARD, transferAmount);

        TransferResponse res = atmService.transfer(TITIKSHA_CARD, req);
        assertTrue(res.isSuccess());
        assertEquals("James Wilson", res.getRecipientName());

        AccountOverviewResponse senderAfter = atmService.getAccountOverview(TITIKSHA_CARD);
        AccountOverviewResponse recipientAfter = atmService.getAccountOverview(JAMES_CARD);

        assertEquals(senderBefore.getBalance().subtract(transferAmount), senderAfter.getBalance());
        assertEquals(recipientBefore.getBalance().add(transferAmount), recipientAfter.getBalance());
    }

    @Test
    void testSelfTransferFails() {
        TransferRequest req = new TransferRequest(TITIKSHA_CARD, new BigDecimal("50.00"));
        assertThrows(SelfTransferException.class, () -> atmService.transfer(TITIKSHA_CARD, req));
    }
}
