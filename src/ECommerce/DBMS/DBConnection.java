package ECommerce.DBMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection connection;

    public DBConnection() {
        // prevent instantiation
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/ecommerce";
                String user = "root";
                String pass = "YOUR_MYSQL_PASSWORD";

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                } catch (ClassNotFoundException e) {
                    System.out.println("[DB] JDBC Driver not found.");
                    connection = null;
                } catch (SQLException e) {
                    System.out.println("[DB] Cannot connect to database. Please check MySQL is running.");
                    connection = null;
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB] Connection is invalid.");
            connection = null;
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[DB] Connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("[DB] Error closing connection.");
            }
        }
    }
}
