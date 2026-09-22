package ECommerce.Model;

import ECommerce.DBMS.DBConnection;
import ECommerce.DS.Stack;

import java.sql.*;
import java.util.Scanner;

public class admin {
    static String ADMIN_USERNAME = "admin";
    static String ADMIN_PASSWORD = "admin123";
    static Scanner sc = new Scanner(System.in);

    //---- Admin Login ----
    public static void showAdminPanel() {
        System.out.println("\n[ADMIN_PANEL_LOGIN]");
        System.out.println("-----------------------------");

        System.out.print(" > Enter Admin Username: ");
        String username = sc.nextLine();
        System.out.print(" > Enter Admin Password: ");
        String password = sc.nextLine();

        if (!username.equals(ADMIN_USERNAME) || !password.equals(ADMIN_PASSWORD)) {
            System.out.println("Access Denied");
            return;
        }

        // OTP verification
        OTP otp = new OTP();
        System.out.println("Your OTP is: " + otp.getCode());
        System.out.println("You have 30 seconds to enter the correct OTP.");

        while (!otp.isExpired()) {
            System.out.print("Enter OTP: ");
            String input = sc.nextLine();

            if (otp.isExpired()) {
                System.out.println("OTP expired. Please request a new one.");
                return;
            }

            if (otp.verify(input)) {
                System.out.println("OTP verified. Access granted.");
                break;
            } else {
                System.out.println("Invalid OTP. Try again.");
            }
        }

        int choice = -1;
        do {
            System.out.println("\n[ADMIN_PANEL_ACTIVE]");
            System.out.println("-----------------------------");
            System.out.println("> (1) View All Customers");
            System.out.println("> (2) View All Sellers");
            System.out.println("> (3) View All Products");
            System.out.println("> (4) View All Orders");
            System.out.println("> (5) View Customer Total Order Count");
            System.out.println("> (6) Delete Seller");
            System.out.println("> (7) View Wallet Transactions");
            System.out.println("> (0) Exit");
            System.out.println("-----------------------------");
            System.out.print("[>>>Choice one]: ");

            try {
                String input = sc.nextLine().trim();   // read full line
                choice = Integer.parseInt(input);      // safely parse integer

                switch (choice) {
                    case 1: viewAllCustomers(); break;
                    case 2: viewAllSellers(); break;
                    case 3: viewAllProducts(); break;
                    case 4: viewAllOrders(); break;
                    case 5: orderCount(); break;
                    case 6: deleteSeller(); break;
                    case 7: viewWalletTransactions(); break;
                    case 0: System.out.println("Exiting Admin Panel..."); break;
                    default: System.out.println("Invalid choice! Try again."); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a number.");
                choice = -1;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                choice = -1;
            }

        } while (choice != 0);
    }


    static void viewAllCustomers() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM customer");

        System.out.println("\n=== ALL CUSTOMERS ===");
        System.out.println("------------------------------------------------------");
        System.out.println("ID | Name | Email | Phone | Address");
        System.out.println("------------------------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("customer_id");
            String name = rs.getString("customer_name");
            String email = rs.getString("email");
            String phone = rs.getString("phone");
            String address = rs.getString("address");

            System.out.println(id + " | " + name + " | " + email + " | " + phone + " | " + address);
        }
        System.out.println("------------------------------------------------------");
    }


    static void viewAllSellers() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM seller ORDER BY seller_id ASC");

        System.out.println("\n=== ALL SELLERS ===");
        System.out.println("------------------------------------------------------");
        System.out.println("ID | Name | Email | Phone | PAN");
        System.out.println("------------------------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("seller_id");
            String name = rs.getString("seller_name");
            String email = rs.getString("email");
            String phone = rs.getString("phone");
            String pan = rs.getString("pan");

            System.out.println(id + " | " + name + " | " + email + " | " + phone + " | " + pan);
        }
        System.out.println("------------------------------------------------------");
    }


    static void viewAllProducts() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM product");

        System.out.println("\n=== ALL PRODUCTS ===");
        System.out.println("------------------------------------------------------");
        System.out.println("ID | Seller | Product | Category | Price | MRP | Discount | Qty");
        System.out.println("------------------------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("product_id");
            String seller = rs.getString("seller_name");
            String product = rs.getString("product_name");
            String category = rs.getString("category");
            double price = rs.getDouble("original_price");
            double mrp = rs.getDouble("mrp");
            double discount = rs.getDouble("discount_price");
            int qty = rs.getInt("quantity");

            // simple alignment using spaces
            System.out.println(
                    id + " | " + seller + " | " + product + " | " +
                            category + " | Rs." + price + " | Rs." + mrp + " | Rs." + discount +
                            " | " + qty
            );
        }
        System.out.println("------------------------------------------------------");
    }


    static void viewAllOrders() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM orders ORDER BY order_date DESC");

        System.out.println("\n=== ALL ORDERS ===");
        System.out.println("------------------------------------------------------");
        System.out.println("OrderNo | Customer | Seller | Product | Qty | Total | Status | Date");
        System.out.println("------------------------------------------------------");

        while (rs.next()) {
            int orderNo = rs.getInt("order_no");
            String customer = rs.getString("customer_name");
            String seller = rs.getString("seller_name");
            String product = rs.getString("product_name");
            int qty = rs.getInt("quantity");
            double total = rs.getDouble("total_price");
            String status = rs.getString("payment_status");
            Timestamp date = rs.getTimestamp("order_date");

            System.out.println(orderNo + " | " + customer + " | " + seller + " | " +
                    product + " | " + qty + " | Rs." + total + " | " +
                    status + " | " + date);
        }
        System.out.println("------------------------------------------------------");
    }


    static void viewWalletTransactions() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM transaction");

        System.out.println("\n=== WALLET TRANSACTIONS ===");
        System.out.println("------------------------------------------------------");
        System.out.println("No | UserType | UserID | Amount | Type | Method | Description | Date");
        System.out.println("------------------------------------------------------");

        while (rs.next()) {
            int no = rs.getInt("transaction_no");
            String userType = rs.getString("user_type");
            int userId = rs.getInt("user_id");
            double amount = rs.getDouble("amount");
            String type = rs.getString("transaction_type");
            String method = rs.getString("payment_method");
            String desc = rs.getString("description");
            Timestamp date = rs.getTimestamp("transaction_date");

            System.out.println(no + " | " + userType + " | " + userId + " | Rs." + amount +
                    " | " + type + " | " + method + " | " + desc + " | " + date);
        }
        System.out.println("------------------------------------------------------");
    }


    static void deleteSeller() throws SQLException {
        Stack steps = new Stack();
        steps.push("Starting seller deletion process");

        System.out.println("\n[DELETE_SELLER]");
        System.out.println("-----------------------------");
        steps.push("Displayed delete seller header");

        viewAllSellers();
        steps.push("Displayed all sellers");

        System.out.print("Enter Seller ID to delete (0 to cancel): ");
        int sellerId = sc.nextInt();
        sc.nextLine();
        steps.push("User entered seller ID: " + sellerId);

        if (sellerId == 0) {
            System.out.println("Operation cancelled.");
            steps.push("Operation cancelled by user");
        } else {
            Connection conn = DBConnection.getConnection();
            steps.push("Database connection established");

            try {
                // --- Check seller existence ---
                PreparedStatement checkStmt = conn.prepareStatement("SELECT seller_name FROM seller WHERE seller_id = ?");
                checkStmt.setInt(1, sellerId);
                ResultSet rs = checkStmt.executeQuery();
                steps.push("Executed query to find seller");

                if (!rs.next()) {
                    System.out.println("Seller with ID " + sellerId + " not found.");
                    steps.push("Seller not found with ID: " + sellerId);
                } else {
                    String sellerName = rs.getString("seller_name");
                    steps.push("Found seller: " + sellerName);

                    System.out.print("Are you sure you want to delete seller '" + sellerName + "'? (yes/no): ");
                    String confirmation = sc.nextLine().toLowerCase();
                    steps.push("User confirmation: " + confirmation);

                    if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                        System.out.println("Deletion cancelled.");
                        steps.push("Deletion cancelled by user");
                    } else {
                        // --- Check for dependencies (products + orders) ---
                        steps.push("Checking for foreign key constraints...");
                        boolean hasDependencies = false;

                        PreparedStatement productCheck = conn.prepareStatement("SELECT COUNT(*) FROM product WHERE seller_id = ?");
                        productCheck.setInt(1, sellerId);
                        ResultSet productRs = productCheck.executeQuery();
                        if (productRs.next() && productRs.getInt(1) > 0) hasDependencies = true;

                        PreparedStatement orderCheck = conn.prepareStatement("SELECT COUNT(*) FROM orders WHERE seller_id = ?");
                        orderCheck.setInt(1, sellerId);
                        ResultSet orderRs = orderCheck.executeQuery();
                        if (orderRs.next() && orderRs.getInt(1) > 0) hasDependencies = true;

                        if (hasDependencies) {
                            System.out.println("Cannot delete seller. This seller has active products or orders in the system.");
                            steps.push("Deletion failed - seller has dependencies");
                        } else {
                            steps.push("No foreign key constraints found");

                            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM seller WHERE seller_id = ?");
                            deleteStmt.setInt(1, sellerId);
                            steps.push("Prepared delete statement");

                            int rowsAffected = deleteStmt.executeUpdate();
                            steps.push("Executed delete statement - rows affected: " + rowsAffected);

                            if (rowsAffected > 0) {
                                System.out.println("Seller '" + sellerName + "' deleted successfully.");
                                steps.push("Seller deleted successfully");

                                PreparedStatement auditStmt = conn.prepareStatement(
                                        "INSERT INTO transaction (user_type, user_id, amount, transaction_type, payment_method, description) " +
                                                "VALUES (?, ?, ?, ?, ?, ?)"
                                );
                                auditStmt.setString(1, "admin");
                                auditStmt.setInt(2, 0);
                                auditStmt.setDouble(3, 0);
                                auditStmt.setString(4, "delete");
                                auditStmt.setString(5, "system");
                                auditStmt.setString(6, "Admin deleted seller: " + sellerName + " (ID: " + sellerId + ")");
                                auditStmt.executeUpdate();
                                steps.push("Audit log entry created");
                            } else {
                                System.out.println("Failed to delete seller.");
                                steps.push("Delete operation failed");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                steps.push("SQLException occurred: " + e.getMessage());
                if (e.getMessage().contains("foreign key constraint")) {
                    System.out.println("Cannot delete seller. This seller has active products or orders in the system.");
                    steps.push("Foreign key constraint violation detected");
                } else {
                    System.out.println("Database error: " + e.getMessage());
                    steps.push("Other database error occurred");
                }
            } finally {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    steps.push("Database connection closed");
                }
            }
        }

        // --- Show all steps at the end ---
        System.out.println("\n[DELETION_PROCESS_STEPS]");
        System.out.println("-----------------------------");
        Stack tempStack = new Stack();
        while (!steps.isEmpty()) tempStack.push(steps.pop());
        int stepNumber = 1;
        while (!tempStack.isEmpty()) {
            System.out.println(stepNumber + ". " + tempStack.pop());
            stepNumber++;
        }
        System.out.println("-----------------------------");
    }

    static void orderCount() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        System.out.println("\n=== CUSTOMER ORDERS COUNT ===");
        System.out.println("------------------------------------------------------");
        System.out.println("Customer | OrderCount");
        System.out.println("------------------------------------------------------");

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("[ERROR] Database connection failed!");
                return;
            }

            st = conn.createStatement();
            rs = st.executeQuery("SELECT customer_id, COUNT(order_no) AS total_order FROM orders GROUP BY customer_id");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String customer = rs.getString("customer_id");
                int order = rs.getInt("total_order");
                System.out.println(customer + " | " + order);
            }

            if (!found) {
                System.out.println("No orders found.");
            }

        } catch (SQLException e) {
            System.out.println("[SQL ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                System.out.println("[CLOSE ERROR] " + e.getMessage());
            }
            System.out.println("------------------------------------------------------");
        }
    }
}