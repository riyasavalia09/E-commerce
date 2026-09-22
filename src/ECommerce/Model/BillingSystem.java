package ECommerce.Model;

import ECommerce.DS.DoubleLinkedList;
import ECommerce.DBMS.DBConnection;
import java.io.*;
import java.sql.*;

public class BillingSystem
{
    public static void generateBill(customer customer, DoubleLinkedList items, double total, int orderNo) {
        String timestamp = "";
        String fileName = "Bill_Order_" + orderNo + "_" + customer.getName() + ".txt";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT order_date FROM orders WHERE order_no = ?")) {
            stmt.setString(1, String.valueOf(orderNo));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                timestamp = rs.getString("order_date");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error fetching order date/time: " + e.getMessage());
        }

        // Generate bill text file
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("================================================\n");
            writer.write("                BHARATIYA JANTA MART\n");
            writer.write("         Your Trusted E-Commerce Partner\n");
            writer.write("================================================\n");

            // Get payment method from database
            String paymentMethod = "Wallet";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT payment_method FROM orders WHERE order_no = ? LIMIT 1")) {
                stmt.setInt(1, orderNo);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    paymentMethod = rs.getString("payment_method");
                }
            } catch (SQLException e) {
                System.out.println("Error fetching payment method: " + e.getMessage());
            }

            // ===== Order info =====
            writer.write("Order ID: ORD" + orderNo + "\n");
            writer.write("Date & Time: " + timestamp + "\n");
            writer.write("Payment Method: " + paymentMethod + "\n");
            writer.write("------------------------------------------------\n");

            // ===== Customer info =====
            writer.write("Customer Name: " + customer.getName() + "\n");
            writer.write("Customer ID: CUST" + customer.getId() + "\n");
            writer.write("Phone: " + customer.getPhone() + "\n");
            writer.write("Address: " + customer.getAddress() + "\n");
            writer.write("------------------------------------------------\n");

            // ===== Items list =====
            writer.write("Item Name           Qty   Price   Amount   Seller\n");
            writer.write("------------------------------------------------\n");

            for (int i = 0; i < items.size(); i++) {
                CartItem item = items.get(i);
                String itemName = item.getProductName();
                String qty = String.valueOf(item.getQuantity());
                String price = roundToTwoDecimals(item.getMrp());
                String amount = roundToTwoDecimals(item.getMrp() * item.getQuantity());
                String seller = item.getSellerName();

                writer.write(itemName);
                addSpaces(writer, 20 - itemName.length());
                writer.write(qty);
                addSpaces(writer, 5 - qty.length());
                writer.write(price);
                addSpaces(writer, 8 - price.length());
                writer.write(amount);
                addSpaces(writer, 10 - amount.length());
                writer.write(seller + "\n");
            }

            // ===== Totals =====
            writer.write("\n------------------------------------------------\n");
            writer.write("Subtotal: ₹" + roundToTwoDecimals(total) + "\n");
            writer.write("------------------------------------------------\n");
            writer.write("Grand Total (INR): ₹" + roundToTwoDecimals(total) + "\n");
            writer.write("------------------------------------------------\n");

            // ===== Transaction info =====
            writer.write("Transaction ID: TXN" + orderNo + "_" + System.currentTimeMillis() + "\n");
            writer.write("------------------------------------------------\n");

            // ===== Footer =====
            if (paymentMethod.equals("Cash on Delivery")) {
                writer.write("NOTE: Payment of ₹" + roundToTwoDecimals(total) + " will be collected at delivery.\n");
            }
            writer.write("Notes: Delivery expected in 3-5 business days.\n");
            writer.write("------------------------------------------------\n");
            writer.write("Thank you for shopping with us! \n");
            writer.write("Track your order at: www.bharatiyajantamart.com/track/ORD" + orderNo + "\n");
            writer.write("Helpline: 1800-123-456\n");
            writer.write("================================================\n");

            System.out.println("Bill generated successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating bill: " + e.getMessage());
        }
    }

    public static String roundToTwoDecimals(double value) {
        long factor = 100;
        long tmp = Math.round(value * factor);
        double rounded = (double) tmp / factor;
        return String.valueOf(rounded);
    }

    // Helper function for spacing
    public static void addSpaces(FileWriter writer, int count) throws IOException {
        for (int i = 0; i < count; i++) {
            writer.write(" ");
        }
    }
}
