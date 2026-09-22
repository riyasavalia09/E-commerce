package ECommerce.Model;

import ECommerce.DBMS.*;
import ECommerce.DS.DoubleLinkedList;
import java.sql.*;
import java.util.*;

public class customer extends User {
    public String address;
    static DoubleLinkedList cart = new DoubleLinkedList();
    public static Scanner sc = new Scanner(System.in);

    public customer(String name, String email, String phone, String address, String password) {
        super(name, email, phone, password);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    // === LOGIN ===
    public boolean login() {
        System.out.println("\n[CUSTOMER_LOGIN]");
        System.out.println("-----------------------------");

        String inputEmail = "";
        String inputPassword = "";

        while (true) {
            System.out.print("> Enter Email: ");
            inputEmail = sc.nextLine();
            if (validator.isValidEmail(inputEmail)) break;
            System.out.println("> Invalid email format!");
        }

        System.out.print("> Enter Password: ");
        inputPassword = sc.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM customer WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, inputEmail);
            ps.setString(2, inputPassword);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("> Invalid email or password.");
                return false;
            }

            // Set user details
            this.id = rs.getInt("customer_id");
            this.name = rs.getString("customer_name");
            this.email = rs.getString("email");
            this.phone = rs.getString("phone");
            this.address = rs.getString("address");
            this.password = rs.getString("password");
            this.walletPassword = rs.getString("wallet_password");
            this.walletAmount = rs.getDouble("wallet_amount");

            // OTP Verification
            OTP otp = new OTP();
            System.out.println("> Your OTP is: " + otp.getCode() + " (valid 30s)");

            boolean otpVerified = false;
            while (!otp.isExpired()) {
                System.out.print("> Enter OTP: ");
                String inputOtp = sc.nextLine();
                if (otp.verify(inputOtp)) {
                    System.out.println("> OTP Verified!");
                    otpVerified = true;
                    break;
                } else {
                    System.out.println("> Invalid OTP. Try again.");
                }
            }

            if (!otpVerified) {
                System.out.println("> OTP Expired / Verification Failed. Login Aborted.");
                return false;
            }

            System.out.println("> Login successful! Welcome, " + this.name + ".");
            return true;

        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
            return false;
        }
    }

    // === SIGN UP ===
    public static void signUp() {
        System.out.println("\n[CUSTOMER_SIGNUP]");
        System.out.println("-----------------------------");
        String name, email, phone, address, password, walletPassword;

        // Name
        while (true) {
            System.out.print("> Enter Name: ");
            name = sc.nextLine();
            if (validator.isValidName(name)) break;
        }

        // Email
        while (true) {
            System.out.print("> Enter Email: ");
            email = sc.nextLine();
            if (validator.isValidEmail(email)) break;
            System.out.println("> Invalid email format!");
        }

        // Phone
        while (true) {
            System.out.print("> Enter Phone: ");
            phone = sc.nextLine();
            if (validator.isValidPhoneNumber(phone)) break;
            System.out.println("> Phone must be 10 digits!");
        }

        // Address
        while (true) {
            System.out.print("> Enter Address: ");
            address = sc.nextLine();
            if (validator.isValidAddress(address)) break;
            System.out.println("> Invalid Address!");
        }

        // Password
        while (true) {
            System.out.print("> Enter Password (min 6 chars): ");
            password = sc.nextLine();
            if (validator.isValidPassword(password, 8)) break;
            System.out.println("> Password must be at least 6 characters!");
        }

        // Wallet Password
        while (true) {
            System.out.print("> Set Wallet Password (min 4 chars): ");
            walletPassword = sc.nextLine();
            if (validator.isValidPin(walletPassword)) break;
            System.out.println("> Wallet password must be at least 4 characters!");
        }

        // Call stored procedure to insert into database
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{call RegisterCustomer(?, ?, ?, ?, ?, ?)}")) { // Call the stored procedure
            cstmt.setString(1, name);
            cstmt.setString(2, email);
            cstmt.setString(3, phone);
            cstmt.setString(4, address);
            cstmt.setString(5, password);
            cstmt.setString(6, walletPassword);
            cstmt.execute(); // Execute the stored procedure
            System.out.println("> Registration Successful!");
            // The trigger 'after_customer_insert_log' would automatically run here
            // to log the initial wallet setup.
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("> Email already registered.");
            } else {
                System.out.println("> Database error: " + e.getMessage());
            }
        }
        System.out.println("-----------------------------");
    }

    // === CUSTOMER MENU ===
    public void customerMenu() {
        int choice;
        while (true) {
            System.out.println("\n[CUSTOMER_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) View Products");
            System.out.println("> (2) Cart");
            System.out.println("> (3) Orders");
            System.out.println("> (4) Wallet");
            System.out.println("> (5) Edit Profile");
            System.out.println("> (6) Delete Account");
            System.out.println("> (0) Logout");
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
                    viewProductsByCategory();
                    break;
                case 2:
                    cartMenu();
                    break;
                case 3:
                    ordersMenu();
                    break;
                case 4:
                    walletOperations();
                    break;
                case 5:
                    editProfile();
                    break;
                case 6:
                    deleteAccount();
                    break;
                case 0:
                    System.out.println("Logging out of Customer Panel...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }

        }
    }

    @Override
    public void viewProfile() {
        System.out.println("\n[PROFILE]");
        System.out.println("-----------------------------");
        System.out.println("ID: " + this.id);
        System.out.println("Name: " + this.name);
        System.out.println("Email: " + this.email);
        System.out.println("Phone: " + this.phone);
        System.out.println("Address: " + this.address);
        System.out.println("Wallet Balance: ₹" + this.walletAmount);
        System.out.println("-----------------------------");
    }

     void cartMenu() {
        int choice;
        while (true) {
            System.out.println("\n[CART_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Add Product to Cart");
            System.out.println("> (2) Delete Product from Cart by Name");
            System.out.println("> (3) View Cart");
            System.out.println("> (4) Back");
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
                    addProductToCartByCategory();
                    break;
                case 2:
                    deleteProductFromCartByName();
                    break;
                case 3:
                    viewCart();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }

        }
    }

    void ordersMenu() {
        int choice;
        while (true) {
            System.out.println("\n[ORDERS_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Place Order");
            System.out.println("> (2) View Order History");
            System.out.println("> (3) Replace/Return Order"); // NEW
            System.out.println("> (4) Back");
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
                case 1: orderplace(); break;
                case 2: viewOrderHistory(); break;
                case 3: replaceOrReturnOrder(); break; // NEW
                case 4: return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }
        }
    }

     void walletOperations() {
        int choice;
        do {
            System.out.println("\n[WALLET_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) View Wallet Balance");
            System.out.println("> (2) Add Money to Wallet");
            System.out.println("> (3) View All Transactions");
            System.out.println("> (0) Back to Main Menu");
            System.out.println("-----------------------------");
            System.out.print("[>>>Choice One]: ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid Input! Please enter a number.");
                sc.nextLine();
                choice = -1;
                continue;
            }

            switch (choice) {
                case 1:
                    double balance = this.getWalletAmount();
                    balance = Math.round(balance * 100) / 100.0;
                    System.out.println("> Current Wallet Balance: ₹" + balance);
                    break;
                case 2:
                    addMoneyToWallet();
                    break;
                case 3:
                    viewMyTransactions();
                    break;
                case 0:
                    System.out.println("> Returning to Customer Menu...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
                    break;
            }

        } while (choice != 0);
    }

    public void viewProductsByCategory() {
        try (Connection con = DBConnection.getConnection()) {
            // Fetch categories
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT category FROM product WHERE quantity > 0");

            List<String> categories = new ArrayList<>();
            int i = 1;
            System.out.println("\n[PRODUCT_CATEGORIES]");
            System.out.println("-----------------------------");
            while (rs.next()) {
                String cat = rs.getString("category");
                categories.add(cat);
                System.out.println("> (" + i + ") " + cat);
                i++;
            }

            if (categories.isEmpty()) {
                System.out.println("> No categories available.");
                return;
            }

            // Choose category
            System.out.println("-----------------------------");
            System.out.print("[>>>Choose a category (0 to cancel)]: ");
            int ch = sc.nextInt();
            sc.nextLine();
            if (ch == 0 || ch > categories.size()) return;

            String selectedCat = categories.get(ch - 1);

            // Fetch products in chosen category - FIXED column names
            PreparedStatement ps = con.prepareStatement(
                    "SELECT product_id, product_name, mrp, discount_price, quantity, seller_name " +
                            "FROM product WHERE category=? AND quantity > 0");
            ps.setString(1, selectedCat);
            rs = ps.executeQuery();

            System.out.println("\n[PRODUCTS_IN_CATEGORY: " + selectedCat + "]");
            System.out.println("ID | Name | Category | Price | Quantity | Seller");
            System.out.println("-----------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("product_id"); // FIXED: product_id instead of id
                String name = rs.getString("product_name");
                double mrp = rs.getDouble("mrp");
                double discount = rs.getDouble("discount_price");
                int qty = rs.getInt("quantity");
                String seller = rs.getString("seller_name");

                String price = (discount < mrp) ? "Rs." + discount + " (Was Rs." + mrp + ")" : "Rs." + mrp;
                System.out.println(id + " | " + name + " | " + selectedCat + " | " +
                        price + " | " + qty + " | " + seller);
            }
            System.out.println("-----------------------------------------");
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void addProductToCartByCategory() {
        try (Connection con = DBConnection.getConnection()) {
            // Fetch categories
            Map<Integer, String> categories = new HashMap<>();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT category FROM product WHERE quantity > 0");

            System.out.println("\n[SELECT_CATEGORY]");
            System.out.println("-----------------------------");
            int ci = 1;
            while (rs.next()) {
                categories.put(ci, rs.getString("category"));
                System.out.println("> (" + ci + ") " + rs.getString("category"));
                ci++;
            }

            if (categories.isEmpty()) {
                System.out.println("> No categories available.");
                return;
            }

            // Choose category
            System.out.println("-----------------------------");
            System.out.print("[>>>Choose a category (0 to cancel)]: ");
            int ch = sc.nextInt();
            sc.nextLine();
            if (ch == 0 || !categories.containsKey(ch)) return;

            String cat = categories.get(ch);

            // Fetch products in chosen category - FIXED column names
            PreparedStatement ps = con.prepareStatement(
                    "SELECT product_id, product_name, mrp, discount_price, quantity, seller_name " +
                            "FROM product WHERE category=? AND quantity > 0"
            );
            ps.setString(1, cat);
            rs = ps.executeQuery();

            Map<Integer, Integer> indexToId = new HashMap<>();
            System.out.println("\n[PRODUCTS_IN_CATEGORY: " + cat + "]");
            System.out.println("-----------------------------");
            int pi = 1;
            while (rs.next()) {
                int id = rs.getInt("product_id"); // FIXED: product_id instead of id
                String name = rs.getString("product_name");
                double mrp = rs.getDouble("mrp");
                double disc = rs.getDouble("discount_price");
                int stock = rs.getInt("quantity");
                String seller = rs.getString("seller_name");

                indexToId.put(pi, id);
                String price = (disc < mrp) ? "₹" + disc + " (Was ₹" + mrp + ")" : "₹" + mrp;

                System.out.println("> (" + pi + ") " + name + " | " + price +
                        " | Stock: " + stock + " | Seller: " + seller);
                pi++;
            }

            if (indexToId.isEmpty()) {
                System.out.println("> No products found in this category.");
                return;
            }

            // Select product and quantity
            System.out.println("-----------------------------");
            System.out.print("[>>>Select product number]: ");
            int sel = sc.nextInt();
            sc.nextLine();
            if (!indexToId.containsKey(sel)) {
                System.out.println("> Invalid product selection.");
                return;
            }

            System.out.print("[>>>Enter quantity]: ");
            int qty = sc.nextInt();
            sc.nextLine();

            // FIXED: Correct column names in query
            PreparedStatement fetch = con.prepareStatement(
                    "SELECT product_id, product_name, seller_name, mrp, discount_price, quantity " +
                            "FROM product WHERE product_id=?"
            );
            fetch.setInt(1, indexToId.get(sel));
            ResultSet p = fetch.executeQuery();

            if (p.next()) {
                int avail = p.getInt("quantity");
                double mrp = p.getDouble("mrp");
                double disc = p.getDouble("discount_price");
                double priceToUse = (disc < mrp) ? disc : mrp;

                if (qty > 0 && qty <= avail) {
                    cart.addToCart(new CartItem(
                            p.getInt("product_id"), // FIXED: product_id instead of id
                            p.getString("product_name"),
                            p.getString("seller_name"),
                            priceToUse,
                            qty
                    ));
                    System.out.println("> Added " + qty + " x " +
                            p.getString("product_name") + " to cart.");
                } else {
                    System.out.println("> Not enough stock available.");
                }
            } else {
                System.out.println("> Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("> Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

     void deleteProductFromCartByName() {
        if (cart.isEmpty()) {
            System.out.println("\n[YOUR_CART]");
            System.out.println("-----------------------------");
            System.out.println("> Your cart is empty.");
            return;
        }

        System.out.println("\n[REMOVE_PRODUCT_FROM_CART]");
        System.out.println("-----------------------------");
        viewCart(); // Show current cart

        System.out.print("\n[>>>Enter the product name to remove]: ");
        String name = sc.nextLine();

        int removedCount = 0;

        // Loop backwards to safely remove items while iterating
        for (int i = cart.size() - 1; i >= 0; i--) {
            CartItem item = cart.get(i);
            if (item.getProductName().equalsIgnoreCase(name)) {
                cart.remove(item);
                removedCount++;
            }
        }

        if (removedCount == 0) {
            System.out.println("> Product '" + name + "' not found in your cart.");
        } else {
            System.out.println("> Removed " + removedCount + " item(s) of '" + name + "' from cart.");
        }
    }

     void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("\n[YOUR_CART]");
            System.out.println("-----------------------------");
            System.out.println("> Your cart is empty.");
            return;
        }

        System.out.println("\n[YOUR_CART]");
        System.out.println("-----------------------------");

        double total = 0.0;

        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            double subtotal = item.getMrp() * item.getQuantity();
            total += subtotal;

            // Round to 2 decimals
            double mrp = Math.round(item.getMrp() * 100) / 100.0;
            double sub = Math.round(subtotal * 100) / 100.0;

            System.out.println("> " + (i + 1) + ". " + item.getProductName() +
                    " | Seller: " + item.getSellerName() +
                    " | MRP: ₹" + mrp +
                    " | Qty: " + item.getQuantity() +
                    " | Subtotal: ₹" + sub);
        }

        System.out.println("-----------------------------");
        System.out.println("> Total: ₹" + Math.round(total * 100) / 100.0);
    }

    public void addMoneyToWallet() {
        System.out.println("\n[ADD_MONEY_TO_WALLET]");
        System.out.println("-----------------------------");
        System.out.print("> Enter amount: ");

        double amount;
        try {
            amount = sc.nextDouble();
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("> Invalid amount");
            sc.nextLine();
            return;
        }

        if (amount <= 0) {
            System.out.println("> Amount must be positive");
            return;
        }

        System.out.println("\n> 1. UPI");
        System.out.println("> 2. Net Banking");
        System.out.println("> 3. Card");
        System.out.println("> 4. Gift Card");
        System.out.print("> Choose: ");

        int ch;
        try {
            ch = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("> Invalid choice");
            sc.nextLine();
            return;
        }

        String method = "";
        String desc = "";

        switch (ch) {
            case 1:
                System.out.print("> UPI ID: ");
                String upi = sc.nextLine();
                System.out.print("> PIN: ");
                String pin = sc.nextLine();
                sc.nextLine();
                method = "UPI";
                desc = "UPI " + upi + " PIN:" + pin;
                break;
            case 2:
                System.out.print("> Bank: ");
                String bank = sc.nextLine();
                System.out.print("> Acc last 4: ");
                String acc = sc.nextLine();
                method = "NetBanking";
                desc = "Bank " + bank + " XXXX" + acc;
                break;
            case 3:
                System.out.print("> Card last 4: ");
                String card = sc.nextLine();
                System.out.print("> Expiry: ");
                String exp = sc.nextLine();
                method = "Card";
                desc = "Card XXXX" + card + " Exp:" + exp;
                break;
            case 4:
                System.out.print("> Gift Card: ");
                String gc = sc.nextLine();
                method = "GiftCard";
                desc = "GiftCard " + gc;
                break;
            default:
                System.out.println("> Invalid method");
                return;
        }

        try (Connection c = DBConnection.getConnection()) {
            // Update wallet amount
            PreparedStatement u = c.prepareStatement(
                    "UPDATE customer SET wallet_amount = wallet_amount + ? WHERE customer_id = ?"
            );
            u.setDouble(1, amount);
            u.setInt(2, id);

            if (u.executeUpdate() > 0) {
                // Record transaction
                PreparedStatement t = c.prepareStatement(
                        "INSERT INTO transaction (user_type, user_id, amount, transaction_type, payment_method, description) " +
                                "VALUES (?, ?, ?, ?, ?, ?)"
                );
                t.setString(1, "customer");
                t.setInt(2, this.id);
                t.setDouble(3, amount);
                t.setString(4, "credit");
                t.setString(5, method);
                t.setString(6, desc);
                t.executeUpdate();
                t.close();

                walletAmount += amount;
                System.out.println("> Added ₹" + amount + " | Balance: ₹" + walletAmount);
            } else {
                System.out.println("> Failed to update wallet");
            }

            u.close();
        } catch (SQLException e) {
            System.out.println("> DB Error: " + e.getMessage());
        }
    }

     void viewMyTransactions() {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM transaction WHERE user_type = 'customer' AND user_id = ? ORDER BY transaction_date DESC");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n[MY_TRANSACTIONS]");
            System.out.println("-----------------------------");

            if (!rs.isBeforeFirst()) {
                System.out.println("> No transactions found.");
                return;
            }

            System.out.println("Amount | Type | Method | Date | Description");
            System.out.println("-----------------------------------------");

            while (rs.next()) {
                String type = rs.getString("transaction_type");
                double amountVal = rs.getDouble("amount");
                String sign = type.equals("debit") ? "-" : "";
                String amount = sign + "₹" + amountVal;

                String method = rs.getString("payment_method");
                String date = rs.getTimestamp("transaction_date").toString();
                String desc = rs.getString("description");

                System.out.println("> " + amount + " | " + type + " | " +
                        method + " | " + date + " | " + desc);
            }
            System.out.println("-----------------------------------------");
        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
        }
    }

    void orderplace() {
        if (cart.isEmpty()) {
            System.out.println("\n[PLACE_ORDER]");
            System.out.println("> Your cart is empty.");
            return;
        }

        // Calculate total amount first
        double totalAmount = 0.0;
        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            totalAmount += item.getMrp() * item.getQuantity();
        }

        // Display cart summary
        System.out.println("\n[ORDER_SUMMARY]");
        System.out.println("-----------------------------");
        viewCart();
        System.out.println("-----------------------------");
        System.out.println("Total Amount: ₹" + Math.round(totalAmount * 100) / 100.0);
        System.out.println("-----------------------------");

        // Payment method selection
        System.out.println("\n[SELECT_PAYMENT_METHOD]");
        System.out.println("> (1) Wallet Payment");
        System.out.println("> (2) Cash on Delivery");
        System.out.print("[>>>Choose payment method]: ");

        int paymentChoice;
        try {
            paymentChoice = sc.nextInt();
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("> Invalid choice.");
            sc.nextLine();
            return;
        }

        Connection conn = null;
        boolean success = false;
        int orderNo = 0;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("> Database connection failed.");
                return;
            }

            conn.setAutoCommit(false); // Start transaction

            // Process payment based on choice
            if (paymentChoice == 1) {
                // Wallet payment
                if (!processWalletPayment(conn, totalAmount)) {
                    System.out.println("> Payment failed. Order cancelled.");
                    conn.rollback();
                    return;
                }
            } else if (paymentChoice == 2) {
                // Cash on Delivery
                System.out.println("> You have selected Cash on Delivery.");
                System.out.println("> Payment will be collected at the time of delivery.");
            } else {
                System.out.println("> Invalid payment method.");
                conn.rollback();
                return;
            }

            // Process each item in cart
            for (int i = 0; i < cart.size(); i++) {
                CartItem item = cart.get(i);

                // Check stock
                PreparedStatement checkStock = conn.prepareStatement(
                        "SELECT quantity FROM product WHERE product_id = ?");
                checkStock.setInt(1, item.getProductId());
                ResultSet stockRs = checkStock.executeQuery();

                if (!stockRs.next() || stockRs.getInt("quantity") < item.getQuantity()) {
                    System.out.println("> Insufficient stock for: " + item.getProductName());
                    throw new SQLException("Insufficient stock for product: " + item.getProductName());
                }

                // Update stock
                PreparedStatement updateStock = conn.prepareStatement(
                        "UPDATE product SET quantity = quantity - ? WHERE product_id = ?");
                updateStock.setInt(1, item.getQuantity());
                updateStock.setInt(2, item.getProductId());
                updateStock.executeUpdate();

                // Get seller ID
                PreparedStatement sellerStmt = conn.prepareStatement(
                        "SELECT seller_id FROM product WHERE product_id = ?");
                sellerStmt.setInt(1, item.getProductId());
                ResultSet sellerRs = sellerStmt.executeQuery();

                int sellerId = 0;
                if (sellerRs.next()) {
                    sellerId = sellerRs.getInt("seller_id");
                } else {
                    throw new SQLException("Seller not found for product");
                }

                // Insert order
                PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO orders (customer_id, customer_name, seller_id, seller_name, " +
                                "product_id, product_name, quantity, total_price, payment_status, payment_method) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, this.id);
                orderStmt.setString(2, this.name);
                orderStmt.setInt(3, sellerId);
                orderStmt.setString(4, item.getSellerName());
                orderStmt.setInt(5, item.getProductId());
                orderStmt.setString(6, item.getProductName());
                orderStmt.setInt(7, item.getQuantity());
                orderStmt.setDouble(8, item.getMrp() * item.getQuantity());
                orderStmt.setString(9, paymentChoice == 1 ? "completed" : "pending");
                orderStmt.setString(10, paymentChoice == 1 ? "Wallet" : "Cash on Delivery");
                orderStmt.executeUpdate();

                // Get order number for the first product
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next() && orderNo == 0) {
                    orderNo = generatedKeys.getInt(1);
                }

                // Record transaction for wallet payments
                if (paymentChoice == 1) {
                    PreparedStatement transStmt = conn.prepareStatement(
                            "INSERT INTO transaction (user_type, user_id, amount, transaction_type, payment_method, description) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)");
                    transStmt.setString(1, "customer");
                    transStmt.setInt(2, this.id);
                    transStmt.setDouble(3, item.getMrp() * item.getQuantity());
                    transStmt.setString(4, "debit");
                    transStmt.setString(5, "Wallet");
                    transStmt.setString(6, "Order #" + orderNo + " for " + item.getProductName());
                    transStmt.executeUpdate();
                }
            }

            conn.commit();
            success = true;
            System.out.println("> All orders placed successfully!");
            System.out.println("> Order Number: " + orderNo);

            // Generate bill for all products
            BillingSystem.generateBill(this, cart, totalAmount, orderNo);

            cart.clear();

        } catch (SQLException e) {
            System.out.println("> Order failed: " + e.getMessage());

            // Rollback transaction if connection is still open
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("> Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    System.out.println("> Error during rollback: " + rollbackEx.getMessage());
                }
            }

            // Refund payment using a NEW connection (only for wallet payments)
            if (paymentChoice == 1) {
                try (Connection refundConn = DBConnection.getConnection()) {
                    if (refundConn != null) {
                        PreparedStatement refundStmt = refundConn.prepareStatement(
                                "UPDATE customer SET wallet_amount = wallet_amount + ? WHERE customer_id = ?");
                        refundStmt.setDouble(1, totalAmount);
                        refundStmt.setInt(2, this.id);
                        int rowsUpdated = refundStmt.executeUpdate();

                        if (rowsUpdated > 0) {
                            walletAmount += totalAmount;
                            System.out.println("> Payment refunded successfully.");
                        } else {
                            System.out.println("> Refund failed - customer not found.");
                        }
                    }
                } catch (SQLException refundEx) {
                    System.out.println("> Error during refund: " + refundEx.getMessage());
                }
            }
        } finally {
            // Only reset auto-commit and close connection if it's still valid
            if (conn != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.setAutoCommit(true); // Reset auto-commit
                        conn.close(); // Close connection
                    }
                } catch (SQLException e) {
                    System.out.println("> Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public void replaceOrReturnOrder() {
        System.out.println("\n[REPLACE_OR_RETURN_ORDER]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            // Show recent orders
            PreparedStatement orderStmt = conn.prepareStatement(
                    "SELECT DISTINCT order_no, SUM(total_price) as total, order_date " +
                            "FROM orders WHERE customer_name = ? " +
                            "GROUP BY order_no ORDER BY order_date DESC LIMIT 5"
            );
            orderStmt.setString(1, this.name);
            ResultSet orders = orderStmt.executeQuery();

            System.out.println("Your Recent Orders:");
            System.out.println("Order No | Total Amount | Order Date");
            System.out.println("------------------------------------");

            boolean hasOrders = false;
            while (orders.next()) {
                hasOrders = true;
                System.out.println(
                        orders.getInt("order_no") + " | ₹" +
                                orders.getDouble("total") + " | " +
                                orders.getTimestamp("order_date")
                );
            }

            if (!hasOrders) {
                System.out.println("> No orders found!");
                return;
            }
            System.out.println("------------------------------------");

            // Select order
            System.out.print("[>>> Enter Order Number to replace/return]: ");
            int orderNo = sc.nextInt();
            sc.nextLine();

            // Show order items
            PreparedStatement itemStmt = conn.prepareStatement(
                    "SELECT product_id, product_name, seller_name, quantity, total_price " +
                            "FROM orders WHERE order_no = ? AND customer_name = ?"
            );
            itemStmt.setInt(1, orderNo);
            itemStmt.setString(2, this.name);
            ResultSet items = itemStmt.executeQuery();

            System.out.println("\nOrder Items:");
            System.out.println("ID | Product Name | Seller | Qty | Price");
            System.out.println("----------------------------------------");

            boolean hasItems = false;
            while (items.next()) {
                hasItems = true;
                System.out.println(
                        items.getInt("product_id") + " | " +
                                items.getString("product_name") + " | " +
                                items.getString("seller_name") + " | " +
                                items.getInt("quantity") + " | ₹" +
                                items.getDouble("total_price")
                );
            }

            if (!hasItems) {
                System.out.println("> No items found for this order or order doesn't belong to you.");
                return;
            }
            System.out.println("----------------------------------------");

            // Select product to replace/return
            System.out.print("[>>> Enter Product ID to replace/return]: ");
            int productId = sc.nextInt();
            sc.nextLine();

            // Get product details
            PreparedStatement productStmt = conn.prepareStatement(
                    "SELECT product_name, seller_name, quantity, total_price " +
                            "FROM orders WHERE order_no = ? AND product_id = ? AND customer_name = ?"
            );
            productStmt.setInt(1, orderNo);
            productStmt.setInt(2, productId);
            productStmt.setString(3, this.name);
            ResultSet productRs = productStmt.executeQuery();

            if (!productRs.next()) {
                System.out.println("> Product not found in this order.");
                return;
            }

            String productName = productRs.getString("product_name");
            String sellerName = productRs.getString("seller_name");
            int quantity = productRs.getInt("quantity");
            double itemPrice = productRs.getDouble("total_price");

            System.out.println("\nSelected Product: " + productName);
            System.out.println("Seller: " + sellerName);
            System.out.println("Quantity: " + quantity);
            System.out.println("Price: ₹" + itemPrice);

            // Select action
            System.out.println("\n[SELECT_ACTION]");
            System.out.println("> (1) Replace with different product");
            System.out.println("> (2) Return for refund");
            System.out.println("> (3) Cancel");
            System.out.print("[>>>Choose action]: ");

            int action = sc.nextInt();
            sc.nextLine();

            if (action == 3) {
                System.out.println("> Operation cancelled.");
                return;
            }

            // Process replacement or return
            conn.setAutoCommit(false);

            try {
                if (action == 1) {
                    // Replace with different product
                    processReplacement(conn, orderNo, productId, productName, quantity, itemPrice);
                } else if (action == 2) {
                    // Return for refund
                    processReturn(conn, orderNo, productId, productName, quantity, itemPrice);
                } else {
                    System.out.println("> Invalid action.");
                    conn.rollback();
                    return;
                }

                conn.commit();
                System.out.println("> Operation completed successfully!");

            } catch (Exception e) {
                conn.rollback();
                System.out.println("> Operation failed: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
            sc.nextLine();
        }
    }

    public void processReplacement(Connection conn, int orderNo, int oldProductId,
                                    String oldProductName, int quantity, double oldPrice)
            throws SQLException {

        System.out.println("\n[SELECT_REPLACEMENT_PRODUCT]");
        System.out.println("-----------------------------");

        // Show available products from the same seller
        PreparedStatement sellerStmt = conn.prepareStatement(
                "SELECT seller_name FROM orders WHERE order_no = ? AND product_id = ?"
        );
        sellerStmt.setInt(1, orderNo);
        sellerStmt.setInt(2, oldProductId);
        ResultSet sellerRs = sellerStmt.executeQuery();

        if (!sellerRs.next()) {
            throw new SQLException("Seller not found for this product.");
        }

        String sellerName = sellerRs.getString("seller_name");

        // Show available products from the same seller
        PreparedStatement availableStmt = conn.prepareStatement(
                "SELECT product_id, product_name, mrp, discount_price, quantity " +
                        "FROM product WHERE seller_name = ? AND quantity > 0 " +
                        "AND product_id != ? ORDER BY product_name"
        );
        availableStmt.setString(1, sellerName);
        availableStmt.setInt(2, oldProductId);
        ResultSet availableRs = availableStmt.executeQuery();

        System.out.println("Available Products from " + sellerName + ":");
        System.out.println("ID | Product Name | Price | Stock");
        System.out.println("---------------------------------");

        boolean hasProducts = false;
        while (availableRs.next()) {
            hasProducts = true;
            double price = availableRs.getDouble("discount_price");
            if (price == 0) price = availableRs.getDouble("mrp");

            System.out.println(
                    availableRs.getInt("product_id") + " | " +
                            availableRs.getString("product_name") + " | ₹" +
                            price + " | " +
                            availableRs.getInt("quantity")
            );
        }

        if (!hasProducts) {
            throw new SQLException("No replacement products available from this seller.");
        }
        System.out.println("---------------------------------");

        // Select replacement product
        System.out.print("[>>> Enter Replacement Product ID]: ");
        int newProductId = sc.nextInt();
        sc.nextLine();

        // Get replacement product details
        PreparedStatement newProductStmt = conn.prepareStatement(
                "SELECT product_name, mrp, discount_price, quantity " +
                        "FROM product WHERE product_id = ? AND seller_name = ?"
        );
        newProductStmt.setInt(1, newProductId);
        newProductStmt.setString(2, sellerName);
        ResultSet newProductRs = newProductStmt.executeQuery();

        if (!newProductRs.next()) {
            throw new SQLException("Replacement product not found.");
        }

        String newProductName = newProductRs.getString("product_name");
        double newPrice = newProductRs.getDouble("discount_price");
        if (newPrice == 0) newPrice = newProductRs.getDouble("mrp");
        int availableStock = newProductRs.getInt("quantity");

        if (availableStock < quantity) {
            throw new SQLException("Not enough stock for replacement. Available: " + availableStock);
        }

        System.out.println("Replacement: " + oldProductName + " → " + newProductName);
        System.out.println("Price Difference: ₹" + oldPrice + " → ₹" + (newPrice * quantity));

        // Confirm replacement
        System.out.print("[>>> Confirm replacement (yes/no)]: ");
        String confirm = sc.nextLine().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            throw new SQLException("Replacement cancelled by user.");
        }

        // Process replacement
        // 1. Update stock for old product (return to inventory)
        PreparedStatement returnStockStmt = conn.prepareStatement(
                "UPDATE product SET quantity = quantity + ? WHERE product_id = ?"
        );
        returnStockStmt.setInt(1, quantity);
        returnStockStmt.setInt(2, oldProductId);
        returnStockStmt.executeUpdate();

        // 2. Update stock for new product (reduce inventory)
        PreparedStatement reduceStockStmt = conn.prepareStatement(
                "UPDATE product SET quantity = quantity - ? WHERE product_id = ?"
        );
        reduceStockStmt.setInt(1, quantity);
        reduceStockStmt.setInt(2, newProductId);
        reduceStockStmt.executeUpdate();

        // 3. Update order record
        PreparedStatement updateOrderStmt = conn.prepareStatement(
                "UPDATE orders SET product_id = ?, product_name = ?, total_price = ? " +
                        "WHERE order_no = ? AND product_id = ?"
        );
        updateOrderStmt.setInt(1, newProductId);
        updateOrderStmt.setString(2, newProductName);
        updateOrderStmt.setDouble(3, newPrice * quantity);
        updateOrderStmt.setInt(4, orderNo);
        updateOrderStmt.setInt(5, oldProductId);
        updateOrderStmt.executeUpdate();

        // 4. Handle price difference
        double priceDifference = (newPrice * quantity) - oldPrice;
        if (priceDifference != 0) {
            handlePriceAdjustment(conn, orderNo, priceDifference,
                    "Replacement adjustment: " + oldProductName + " → " + newProductName);
        }

        System.out.println("> Replacement completed successfully!");
    }

    public void processReturn(Connection conn, int orderNo, int productId,
                               String productName, int quantity, double price)
            throws SQLException {

        System.out.println("\n[PROCESS_RETURN]");
        System.out.println("Returning: " + productName);
        System.out.println("Quantity: " + quantity);
        System.out.println("Refund Amount: ₹" + price);

        // Confirm return
        System.out.print("[>>> Confirm return (yes/no)]: ");
        String confirm = sc.nextLine().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            throw new SQLException("Return cancelled by user.");
        }

        // Process return
        // 1. Return stock to inventory
        PreparedStatement returnStockStmt = conn.prepareStatement(
                "UPDATE product SET quantity = quantity + ? WHERE product_id = ?"
        );
        returnStockStmt.setInt(1, quantity);
        returnStockStmt.setInt(2, productId);
        returnStockStmt.executeUpdate();

        // 2. Process refund
        processRefund(conn, orderNo, price, "Return of: " + productName);

        // 3. Mark item as returned in orders (optional: you might want to keep history)
        PreparedStatement updateOrderStmt = conn.prepareStatement(
                "UPDATE orders SET quantity = 0, total_price = 0 WHERE order_no = ? AND product_id = ?"
        );
        updateOrderStmt.setInt(1, orderNo);
        updateOrderStmt.setInt(2, productId);
        updateOrderStmt.executeUpdate();

        System.out.println("> Return processed successfully! Refund issued.");
    }

    public void processRefund(Connection conn, int orderNo, double amount, String description)
            throws SQLException {

        // Refund to wallet
        PreparedStatement refundStmt = conn.prepareStatement(
                "UPDATE customer SET wallet_amount = wallet_amount + ? WHERE customer_id = ?"
        );
        refundStmt.setDouble(1, amount);
        refundStmt.setInt(2, this.id);
        refundStmt.executeUpdate();

        // Record transaction
        PreparedStatement transStmt = conn.prepareStatement(
                "INSERT INTO transaction (user_type, user_id, amount, transaction_type, " +
                        "payment_method, description) VALUES (?, ?, ?, ?, ?, ?)"
        );
        transStmt.setString(1, "customer");
        transStmt.setInt(2, this.id);
        transStmt.setDouble(3, amount);
        transStmt.setString(4, "credit");
        transStmt.setString(5, "refund");
        transStmt.setString(6, "Order #" + orderNo + " - " + description);
        transStmt.executeUpdate();

        // Update wallet balance in current object
        this.walletAmount += amount;
    }

    public void handlePriceAdjustment(Connection conn, int orderNo, double amount, String description)
            throws SQLException {

        if (amount > 0) {
            // Customer needs to pay more
            if (this.walletAmount < amount) {
                throw new SQLException("Insufficient wallet balance for price adjustment. Needed: ₹" + amount);
            }

            // Deduct from wallet
            PreparedStatement deductStmt = conn.prepareStatement(
                    "UPDATE customer SET wallet_amount = wallet_amount - ? WHERE customer_id = ?"
            );
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, this.id);
            deductStmt.executeUpdate();

            // Record transaction
            PreparedStatement transStmt = conn.prepareStatement(
                    "INSERT INTO transaction (user_type, user_id, amount, transaction_type, " +
                            "payment_method, description) VALUES (?, ?, ?, ?, ?, ?)"
            );
            transStmt.setString(1, "customer");
            transStmt.setInt(2, this.id);
            transStmt.setDouble(3, amount);
            transStmt.setString(4, "debit");
            transStmt.setString(5, "adjustment");
            transStmt.setString(6, "Order #" + orderNo + " - " + description);
            transStmt.executeUpdate();

            this.walletAmount -= amount;

        } else if (amount < 0) {
            // Customer gets refund
            processRefund(conn, orderNo, -amount, description);
        }
    }

    boolean processWalletPayment(Connection conn, double amount) throws SQLException {
        System.out.print("> Enter Wallet Password: ");
        String inputPass = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT wallet_password, wallet_amount FROM customer WHERE customer_id=?"
        );
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("> Customer not found.");
            return false;
        }

        String pass = rs.getString("wallet_password");
        double balance = rs.getDouble("wallet_amount");

        if (!inputPass.equals(pass)) {
            System.out.println("> Incorrect password.");
            return false;
        }
        if (balance < amount) {
            System.out.println("> Insufficient balance. Current: " + balance);
            return false;
        }

        PreparedStatement upd = conn.prepareStatement(
                "UPDATE customer SET wallet_amount = wallet_amount - ? WHERE customer_id=?"
        );
        upd.setDouble(1, amount);
        upd.setInt(2, id);
        upd.executeUpdate();

        walletAmount -= amount;
        System.out.println("> Payment successful!");
        System.out.println("> Remaining Balance: " + (balance - amount));
        return true;
    }


    public void editProfile() {
        System.out.println("\n[EDIT_PROFILE]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Fetch current profile - FIXED column names
            String query = "SELECT customer_name, email, phone, address FROM customer WHERE customer_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("> Profile not found!");
                return;
            }

            // 2. Show current details - FIXED column names
            System.out.println("Current Profile:");
            System.out.println("> 1. Name    : " + rs.getString("customer_name"));
            System.out.println("> 2. Email   : " + rs.getString("email"));
            System.out.println("> 3. Phone   : " + rs.getString("phone"));
            System.out.println("> 4. Address : " + rs.getString("address"));
            System.out.println("-----------------------------");

            // 3. Choose field
            System.out.print("[>>>Enter field number to edit (0 to cancel)]: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("> Edit cancelled.");
                return;
            }

            String newValue = "";
            String updateQuery = "";

            switch (choice) {
                case 1: // Name
                    do {
                        System.out.print("> Enter new Name: ");
                        newValue = sc.nextLine();
                        if (newValue.isEmpty()) {
                            System.out.println("> Name cannot be empty.");
                        }
                    } while (newValue.isEmpty());
                    updateQuery = "UPDATE customer SET customer_name = ? WHERE customer_id = ?";
                    name = newValue;
                    break;

                case 2: // Email
                    do {
                        System.out.print("> Enter new Email: ");
                        newValue = sc.nextLine();
                        if (!validator.isValidEmail(newValue)) {
                            System.out.println("> Invalid email format.");
                            newValue = "";
                        }
                    } while (newValue.isEmpty());
                    updateQuery = "UPDATE customer SET email = ? WHERE customer_id = ?";
                    email = newValue;
                    break;

                case 3: // Phone
                    do {
                        System.out.print("> Enter new Phone (10 digits): ");
                        newValue = sc.nextLine();
                        if (!validator.isValidPhoneNumber(newValue)) {
                            System.out.println("> Phone must be 10 digits.");
                            newValue = "";
                        }
                    } while (newValue.isEmpty());
                    updateQuery = "UPDATE customer SET phone = ? WHERE customer_id = ?";
                    phone = newValue;
                    break;

                case 4: // Address
                    do {
                        System.out.print("> Enter new Address: ");
                        newValue = sc.nextLine();
                        if (newValue.isEmpty()) {
                            System.out.println("> Address cannot be empty.");
                        }
                    } while (newValue.isEmpty());
                    updateQuery = "UPDATE customer SET address = ? WHERE customer_id = ?";
                    address = newValue;
                    break;

                default:
                    System.out.println("> Invalid choice.");
                    return;
            }

            // 4. Run update
            PreparedStatement psUpdate = conn.prepareStatement(updateQuery);
            psUpdate.setString(1, newValue);
            psUpdate.setInt(2, id);

            if (psUpdate.executeUpdate() > 0) {
                System.out.println("> Profile updated successfully.");
            } else {
                System.out.println("> Failed to update profile.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("> Email already exists!");
            } else {
                System.out.println("> Database error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    void viewOrderHistory() {
        try (Connection conn = DBConnection.getConnection()) {
            String historyQuery = "SELECT DISTINCT order_no, SUM(total_price) as total, order_date " +
                    "FROM orders WHERE customer_name = ? GROUP BY order_no ORDER BY order_date DESC";
            PreparedStatement psHistory = conn.prepareStatement(historyQuery);
            psHistory.setString(1, name);
            ResultSet orders = psHistory.executeQuery();

            System.out.println("\n[ORDER_HISTORY]");
            System.out.println("-----------------------------");
            boolean hasOrders = false;
            while (orders.next()) {
                hasOrders = true;
                System.out.println(
                        "Order #" + orders.getInt("order_no") +
                                " | Total: ₹" + orders.getDouble("total") +
                                " | Date: " + orders.getTimestamp("order_date")
                );
            }

            if (!hasOrders) {
                System.out.println("> No orders found!");
                return;
            }
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void deleteAccount() {
        User.deleteAccount("Customer", "customer", this.id, this.name, this.password);
    }

}