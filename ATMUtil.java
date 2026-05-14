package atm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper utilities — PIN hashing, formatting, reference generation.
 */
public class ATMUtil {

    // SHA-256 hash a PIN (matches MySQL SHA2 function)
    public static String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Format currency
    public static String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }

    // Generate unique transaction reference
    public static String generateTxnRef() {
        return "TXN" + System.currentTimeMillis();
    }

    // Current timestamp string
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss"));
    }

    // Mask card number for display
    public static String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    // Print a styled divider line
    public static void divider() {
        System.out.println("  " + "─".repeat(46));
    }

    // Print a thick divider
    public static void thickDivider() {
        System.out.println("  " + "═".repeat(46));
    }

    // Center text within width
    public static String center(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, pad)) + text;
    }
}
