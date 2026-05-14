package atm.service;

import atm.dao.AccountDAO;
import atm.dao.UserDAO;
import atm.model.Account;
import atm.model.Transaction;
import atm.model.User;
import atm.util.ATMUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * ATMService - Business logic layer between UI and database.
 * All ATM operations go through this class.
 */
public class ATMService {

    private final UserDAO    userDAO    = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    private User    currentUser    = null;
    private Account currentAccount = null;
    private int     sessionId      = -1;

    // ─── Authentication ─────────────────────────────────────────────

    public boolean login(String cardNumber, String pin) throws SQLException {
        User user = userDAO.authenticate(cardNumber, pin);
        if (user == null) return false;
        if (!user.isActive()) throw new IllegalStateException("CARD_BLOCKED");

        currentUser    = user;
        currentAccount = accountDAO.getAccountByUserId(user.getUserId());
        sessionId      = userDAO.logSession(cardNumber);
        return true;
    }

    public void logout() throws SQLException {
        if (sessionId != -1) {
            userDAO.closeSession(sessionId);
        }
        currentUser    = null;
        currentAccount = null;
        sessionId      = -1;
    }

    // ─── Balance ────────────────────────────────────────────────────

    public double getBalance() {
        return currentAccount.getBalance();
    }

    public String getAccountNo() {
        return currentAccount.getAccountNo();
    }

    public String getAccountType() {
        return currentAccount.getAccountType();
    }

    public String getUserName() {
        return currentUser.getFullName();
    }

    public String getMaskedCard() {
        return ATMUtil.maskCard(currentUser.getCardNumber());
    }

    // ─── Withdrawal ─────────────────────────────────────────────────

    public void withdraw(double amount) throws SQLException {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount % 10 != 0)
            throw new IllegalArgumentException("Amount must be in multiples of $10.");
        if (amount > currentAccount.getBalance())
            throw new IllegalArgumentException("Insufficient funds. Available: " +
                    ATMUtil.formatMoney(currentAccount.getBalance()));

        accountDAO.withdraw(currentAccount, amount);
    }

    // ─── Deposit ────────────────────────────────────────────────────

    public void deposit(double amount) throws SQLException {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (amount > 50000)
            throw new IllegalArgumentException("Maximum single deposit is $50,000.");

        accountDAO.deposit(currentAccount, amount);
    }

    // ─── Transfer ───────────────────────────────────────────────────

    public String transfer(String recipientCardNo, double amount) throws SQLException {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero.");
        if (recipientCardNo.equals(currentUser.getCardNumber()))
            throw new IllegalArgumentException("Cannot transfer to your own account.");
        if (amount > currentAccount.getBalance())
            throw new IllegalArgumentException("Insufficient funds.");

        User recipient = userDAO.findByCardNumber(recipientCardNo);
        if (recipient == null)
            throw new IllegalArgumentException("Recipient card not found.");
        if (!recipient.isActive())
            throw new IllegalArgumentException("Recipient account is inactive.");

        Account recipientAccount = accountDAO.getAccountByUserId(recipient.getUserId());
        accountDAO.transfer(currentAccount, recipientAccount, amount, recipient.getFullName());
        return recipient.getFullName();
    }

    // ─── Mini Statement ─────────────────────────────────────────────

    public List<Transaction> getMiniStatement() throws SQLException {
        return accountDAO.getMiniStatement(currentAccount.getAccountId(), 8);
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
