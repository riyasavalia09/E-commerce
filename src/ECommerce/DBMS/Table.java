package ECommerce.DBMS;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Table {
    public static void createTables() {
        Connection conn = null;
        Statement st = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("Cannot proceed: Database connection failed.");
                return;
            }

            st = conn.createStatement();
            String[] queries = {
                    // Customer Table
                    "CREATE TABLE IF NOT EXISTS customer (" +
                            "customer_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "customer_name VARCHAR(100) NOT NULL, " +
                            "email VARCHAR(100) UNIQUE NOT NULL, " +
                            "phone VARCHAR(15), " +
                            "address VARCHAR(255), " +
                            "password VARCHAR(100) NOT NULL, " +
                            "wallet_password VARCHAR(100), " +
                            "wallet_amount DOUBLE DEFAULT 0.0" +
                            ")",

                    // Seller Table
                    "CREATE TABLE IF NOT EXISTS seller (" +
                            "seller_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "seller_name VARCHAR(100) NOT NULL, " +
                            "email VARCHAR(100) UNIQUE NOT NULL, " +
                            "phone VARCHAR(15), " +
                            "pan VARCHAR(20) UNIQUE NOT NULL, " +
                            "password VARCHAR(100) NOT NULL, " +
                            "wallet_password VARCHAR(100), " +
                            "wallet_amount DOUBLE DEFAULT 0.0" +
                            ")",

                    // Product Table with Foreign Key to Seller
                    "CREATE TABLE IF NOT EXISTS product (" +
                            "product_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "seller_id INT NOT NULL, " +
                            "seller_name VARCHAR(100) NOT NULL, " +
                            "product_name VARCHAR(100) NOT NULL, " +
                            "category VARCHAR(50), " +
                            "original_price DOUBLE NOT NULL, " +
                            "mrp DOUBLE NOT NULL, " +
                            "total_profit DOUBLE, " +
                            "quantity INT NOT NULL, " +
                            "total_quantity_price DOUBLE, " +
                            "discount_price DOUBLE, " +
                            "FOREIGN KEY (seller_id) REFERENCES seller(seller_id) ON DELETE CASCADE" +
                            ")",

                    // Orders Table with Foreign Keys to Customer, Seller, and Product
                    "CREATE TABLE IF NOT EXISTS orders (" +
                            "order_no INT AUTO_INCREMENT PRIMARY KEY, " +
                            "customer_id INT NOT NULL, " +
                            "customer_name VARCHAR(100) NOT NULL, " +
                            "seller_id INT NOT NULL, " +
                            "seller_name VARCHAR(100) NOT NULL, " +
                            "product_id INT NOT NULL, " +
                            "product_name VARCHAR(100) NOT NULL, " +
                            "quantity INT NOT NULL, " +
                            "total_price DOUBLE NOT NULL, " +
                            "payment_status VARCHAR(20), " +
                            "payment_method VARCHAR(20), " +
                            "order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (seller_id) REFERENCES seller(seller_id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE" +
                            ")",

                    // Wallet Transaction Table
                    "CREATE TABLE IF NOT EXISTS transaction (" +
                            "transaction_no INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_type VARCHAR(20) NOT NULL, " +
                            "user_id INT NOT NULL, " +
                            "amount DOUBLE NOT NULL, " +
                            "transaction_type VARCHAR(10) NOT NULL, " +
                            "payment_method VARCHAR(20), " +
                            "description VARCHAR(255), " +
                            "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            };

            for (String query : queries) {
                try {
                    st.executeUpdate(query);
                } catch (SQLException e) {
                    System.out.println("Error executing query: " + query);
                    System.out.println("Error: " + e.getMessage());
                }
            }

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            System.out.println("[DB] Error creating tables: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (st != null) st.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
