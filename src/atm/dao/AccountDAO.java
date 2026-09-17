package atm.dao;

import atm.model.Account;
import atm.model.Transaction;
import atm.util.DBConnection;
import atm.util.ATMUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AccountDAO - handles all account and transaction database queries.
 */
public class AccountDAO {

    /**
     * Get account by user ID.
     */
    public Account getAccountByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("account_id"),
                    rs.getInt("user_id"),
                    rs.getString("account_no"),
                    rs.getDouble("balance"),
                    rs.getString("account_type")
                );
            }
        }
        return null;
    }

    /**
     * Get account by account number (for transfers).
     */
    public Account getAccountByAccountNo(String accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("account_id"),
                    rs.getInt("user_id"),
                    rs.getString("account_no"),
                    rs.getDouble("balance"),
                    rs.getString("account_type")
                );
            }
        }
        return null;
    }

    /**
     * Update account balance in database.
     */
    public void updateBalance(int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    /**
     * Record a transaction in database.
     */
    public void recordTransaction(int accountId, String txnType,
                                   double amount, double balanceAfter,
                                   String description) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, txn_type, amount, balance_after, description, txn_ref) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, txnType);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            ps.setString(5, description);
            ps.setString(6, ATMUtil.generateTxnRef());
            ps.executeUpdate();
        }
    }

    /**
     * Perform withdrawal — updates balance and records transaction atomically.
     */
    public void withdraw(Account account, double amount) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Begin transaction
        try {
            double newBalance = account.getBalance() - amount;
            updateBalance(account.getAccountId(), newBalance);
            recordTransaction(account.getAccountId(), "WITHDRAWAL", amount, newBalance, "ATM CASH WITHDRAWAL");
            conn.commit();
            account.setBalance(newBalance);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Perform deposit — updates balance and records transaction atomically.
     */
    public void deposit(Account account, double amount) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            double newBalance = account.getBalance() + amount;
            updateBalance(account.getAccountId(), newBalance);
            recordTransaction(account.getAccountId(), "DEPOSIT", amount, newBalance, "CASH DEPOSIT");
            conn.commit();
            account.setBalance(newBalance);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Perform fund transfer between two accounts — fully atomic.
     */
    public void transfer(Account from, Account to, double amount, String recipientName) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            double fromNew = from.getBalance() - amount;
            double toNew   = to.getBalance()   + amount;

            updateBalance(from.getAccountId(), fromNew);
            updateBalance(to.getAccountId(),   toNew);

            recordTransaction(from.getAccountId(), "TRANSFER_OUT", amount, fromNew,
                    "TRANSFER TO " + to.getAccountNo() + " - " + recipientName);
            recordTransaction(to.getAccountId(), "TRANSFER_IN", amount, toNew,
                    "TRANSFER FROM " + from.getAccountNo());

            conn.commit();
            from.setBalance(fromNew);
            to.setBalance(toNew);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Fetch last N transactions for mini statement.
     */
    public List<Transaction> getMiniStatement(int accountId, int limit) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY txn_date DESC LIMIT ?";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("txn_id"),
                    rs.getString("txn_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after"),
                    rs.getString("description"),
                    rs.getString("txn_ref"),
                    rs.getString("txn_date")
                ));
            }
        }
        return list;
    }
}
