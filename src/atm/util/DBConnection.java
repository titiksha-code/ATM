package atm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Manages MySQL database connection.
 * Update DB_URL, DB_USER, DB_PASSWORD with your MySQL credentials.
 */
public class DBConnection {

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/globalbank_atm?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";       // change to your MySQL username
    private static final String DB_PASSWORD = "your_password"; // change to your MySQL password

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java.jar to classpath.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
