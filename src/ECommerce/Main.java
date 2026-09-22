package ECommerce;

import java.util.*;
import ECommerce.DBMS.DBConnection;
import ECommerce.DBMS.Table;
import ECommerce.Model.*;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Create all required tables in DB
        Table.createTables();

        System.out.println("\n-----------------------------------------");
        System.out.println(">>> WELCOME E-COMMERCE CONSOLE BASED PLATFORM");
        System.out.println("-----------------------------------------\n");
        System.out.println("-----------------------------------------");
        System.out.println(">>> WELCOME BHARATIYA JANTA MART");
        System.out.println("-----------------------------------------");


        int choice = 0;

        while (true) {
            System.out.println("\n-----------------------------");
            System.out.println("[E-COMMERCE_SYSTEM]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Customer Panel");
            System.out.println("> (2) Seller Panel");
            System.out.println("> (3) Admin Panel");
            System.out.println("> (4) Exit");
            System.out.println("-----------------------------");

            while (true) {
                try {
                    System.out.print("[>>>Choice One]: ");
                    choice = sc.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid Input! Please enter a number.");
                    sc.nextLine();
                }
            }
            sc.nextLine();

            switch (choice) {
                case 1:
                    CustomerPanel();
                    break;
                case 2:
                    SellerPanel();
                    break;
                case 3:
                    admin.showAdminPanel();
                    break;
                case 4:
                    System.out.println("Exiting System... Goodbye!");
                    sc.close();
                    DBConnection.closeConnection();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
                    break;
            }
        }
    }

    public static void CustomerPanel() {
        int choice;
        while (true) {
            System.out.println("\n[CUSTOMER_PANEL]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Login");
            System.out.println("> (2) Sign Up");
            System.out.println("> (3) Exit to Main Menu");
            System.out.println("-----------------------------");
            System.out.print("[>>>Choice One]: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid Input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    customer customer1 = new customer("", "", "", "", "");
                    if (customer1.login()) {
                        customer1.customerMenu();
                    }
                    break;
                case 2:
                    customer.signUp();
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }
        }
    }

    public static void SellerPanel() {
        int choice;
        while (true) {
            System.out.println("\n[SELLER_PANEL]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Login");
            System.out.println("> (2) Sign Up");
            System.out.println("> (3) Exit to Main Menu");
            System.out.println("-----------------------------");
            System.out.print("[>>>Choice One]: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid Input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    seller seller = new seller("", "", "", "", "");
                    if (seller.login()) {
                        seller.sellerMenu();
                    }
                    break;
                case 2:
                    ECommerce.Model.seller.signUp();
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }
        }
    }
}
