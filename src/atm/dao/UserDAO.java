package atm.dao;

import atm.model.User;
import atm.util.DBConnection;
import atm.util.ATMUtil;

import java.sql.*;

/**
 * UserDAO - handles all user/authentication database queries.
 */
public class UserDAO {

    /**
     * Validate card number + PIN against database.
     * Returns the User object if valid, null if not.
     */
    public User authenticate(String cardNumber, String pin) throws SQLException {
        String hashedPin = ATMUtil.hashPin(pin);
        String sql = "SELECT user_id, card_number, full_name, is_active " +
                     "FROM users WHERE card_number = ? AND pin_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            ps.setString(2, hashedPin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("card_number"),
                    rs.getString("full_name"),
                    rs.getBoolean("is_active")
                );
            }
        }
        return null;
    }

    /**
     * Find a user by card number (for transfers).
     */
    public User findByCardNumber(String cardNumber) throws SQLException {
        String sql = "SELECT user_id, card_number, full_name, is_active FROM users WHERE card_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("card_number"),
                    rs.getString("full_name"),
                    rs.getBoolean("is_active")
                );
            }
        }
        return null;
    }

    /**
     * Log ATM session to database.
     */
    public int logSession(String cardNumber) throws SQLException {
        String sql = "INSERT INTO atm_sessions (card_number, status) VALUES (?, 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cardNumber);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    /**
     * Close ATM session.
     */
    public void closeSession(int sessionId) throws SQLException {
        String sql = "UPDATE atm_sessions SET logout_time = NOW(), status = 'CLOSED' WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        }
    }
}
