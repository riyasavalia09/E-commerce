// User.java (Abstract Base Class)
package ECommerce.Model;

import ECommerce.DS.Stack;
import ECommerce.DBMS.DBConnection;
import java.sql.*;
import java.util.Scanner;

interface ProductManagement {
    void addProduct();
    void viewAllProductsByCategory();
    void updateProductDetails();
    void deleteProduct();
}

public abstract class User {
    public int id;
    public String name;
    public String email;
    public String phone;
    public String password;
    public String walletPassword;
    public double walletAmount;

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Abstract methods
    public abstract boolean login();
    public abstract void viewProfile();
    public abstract void editProfile();
    public abstract void addMoneyToWallet();

    // Common methods
    public double getWalletAmount() {
        return walletAmount;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

     static Scanner sc = new Scanner(System.in);

    public static void deleteAccount(String userType, String tableName, int userId, String userName, String password) {
        Stack steps = new Stack();

        System.out.println("\n[DELETE_" + userType.toUpperCase() + "_ACCOUNT]");
        System.out.println("WARNING: This action is permanent and cannot be undone!\n");

        // Step 1: Password check
        steps.push("PASSWORD_CONFIRMATION");
        System.out.print("Enter password: ");
        if (!sc.nextLine().equals(password)) {
            System.out.println("Wrong password. Cancelled.");
            return;
        }

        // Step 2: Final confirm
        steps.push("FINAL_CONFIRMATION");
        System.out.print("DELETE MY ACCOUNT to confirm (type (yes/no)): ");
        if (!sc.nextLine().equals("yes")) {
            System.out.println("Cancelled.");
            return;
        }

        // Step 3: DB delete
        steps.push("PROCESSING_DELETION");
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM " + tableName + " WHERE " + tableName + "_id=?"
            );
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                steps.push("ACCOUNT_DELETED");

                PreparedStatement audit = conn.prepareStatement(
                        "INSERT INTO transaction(user_type,user_id,amount,transaction_type,payment_method,description) " +
                                "VALUES('system',0,0,'system','system',?)"
                );
                audit.setString(1, userType + " deleted: " + userName + " (ID " + userId + ")");
                audit.executeUpdate();

                steps.push("AUDIT_LOG_ADDED");
                System.out.println("Account deleted successfully!");
            } else {
                steps.push("DELETE_FAILED");
                System.out.println("Delete failed.");
            }
        } catch (Exception e) {
            steps.push("ERROR:" + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }

        // Show steps
        showSteps(steps);
    }

    public static void showSteps(Stack steps) {
        System.out.println("\n[STEPS]");
        Stack temp = new Stack();
        while (!steps.isEmpty()) temp.push(steps.pop());
        int i = 1;
        while (!temp.isEmpty()) System.out.println(steps.push(temp.pop())+ " ."+(i++));
    }
}