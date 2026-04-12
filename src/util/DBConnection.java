package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to check database connection.", e);
        }

        try {
            String connectionString = PropertyUtil.getPropertyString();
            if (connectionString == null || connectionString.trim().isEmpty()) {
                throw new IllegalStateException("Database properties are missing or incomplete.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(connectionString);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Unable to connect to database. Check MySQL and util/db.properties.", e);
        }
    }
}
