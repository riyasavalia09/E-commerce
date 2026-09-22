package ECommerce.Model;

import ECommerce.DBMS.DBConnection;
import ECommerce.DS.Stack;
import java.sql.*;
import java.util.*;

public class seller extends User implements ProductManagement {
    public String pan;
    public static Scanner sc = new Scanner(System.in);

    public seller(String name, String email, String phone, String pan, String password) {
        super(name, email, phone, password);
        this.pan = pan;
    }


    public boolean login() {
        System.out.println("[SELLER_LOGIN]");
        System.out.println("-----------------------------");

        // Input credentials
        while (true) {
            System.out.print("> Enter Email: ");
            email = sc.nextLine();
            if (validator.isValidEmail(email)) break;
            System.out.println("> Invalid email format!");
        }
        System.out.print("> Enter Password: ");
        String password = sc.nextLine();

        // --- OTP Verification ---
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

        // If OTP fails
        if (!otpVerified) {
            System.out.println("> OTP Expired / Verification Failed. Login Aborted.");
            System.out.println("-----------------------------");
            return false;
        }

        // --- DB Check for seller login ---
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM seller WHERE email=? AND password=?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);



            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.id = rs.getInt("seller_id");
                this.name = rs.getString("seller_name");
                this.email = rs.getString("email");
                this.phone = rs.getString("phone");
                this.pan = rs.getString("pan");
                this.password = rs.getString("password");
                this.walletPassword = rs.getString("wallet_password");
                this.walletAmount = rs.getDouble("wallet_amount");

                System.out.println("> Login Successful! Welcome, " + this.name + ".");
                System.out.println("-----------------------------");
                return true;
            } else {
                System.out.println("> Invalid Email or Password.");
            }
        } catch (SQLException e) {
            System.out.println("> DB Error: " + e.getMessage());
        }

        System.out.println("-----------------------------");
        return false;
    }

    // Seller Sign Up Method
    public static void signUp() {
        System.out.println("[SELLER_SIGNUP]");
        System.out.println("-----------------------------");

        String name, email, phone, pan, password, walletPassword;

        // Input seller details with validation
        do {
            System.out.print("> Enter Name: ");
            name = sc.nextLine();
        } while (!validator.isValidName(name));

        do {
            System.out.print("> Enter Email: ");
            email = sc.nextLine();
        } while (!validator.isValidEmail(email));

        do {
            System.out.print("> Enter Phone (10 digits): ");
            phone = sc.nextLine();
        } while (!validator.isValidPhoneNumber(phone));

        do {
            System.out.print("> Enter PAN: ");
            pan = sc.nextLine();
        } while (!validator.isValidPan(pan));

        do {
            System.out.print("> Enter Password (min 6 chars): ");
            password = sc.nextLine();
        } while (!validator.isValidPassword(password, 6));

        do {
            System.out.print("> Set Wallet Password (min 4 chars): ");
            walletPassword = sc.nextLine();
        } while (!validator.isValidPin(walletPassword));

        // Insert details into DB
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall("{call RegisterSeller(?, ?, ?, ?, ?, ?)}")) // Call the stored procedure
        {
            cstmt.setString(1, name);
            cstmt.setString(2, email);
            cstmt.setString(3, phone);
            cstmt.setString(4, pan);
            cstmt.setString(5, password);
            cstmt.setString(6, walletPassword);
            cstmt.execute(); // Execute the stored procedure
            System.out.println("> Registration Successful!");
            // The trigger 'after_seller_insert_log' would automatically run here
            // to log the initial wallet setup.
        }
        catch (Exception e)
        {
            String msg = e.getMessage();
            if (msg.contains("Duplicate entry") && msg.contains("email"))
            {
                System.out.println("> Email already registered.");
            }
            else if (msg.contains("Duplicate entry") && msg.contains("pan"))
            {
                System.out.println("> PAN already registered.");
            }
            else
            {
                System.out.println("> DB Error: " + msg);
            }
        }
        System.out.println("-----------------------------");
    }

    // Seller main menu (after login)
    public void sellerMenu()
    {
        int choice = 0;
        while (true)
        {
            // Menu options
            System.out.println("\n[SELLER_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Product Management");
            System.out.println("> (2) View Orders");
            System.out.println("> (3) Sales & Reports");
            System.out.println("> (4) View Customers");
            System.out.println("> (5) Wallet");
            System.out.println("> (6) Edit Profile");
            System.out.println("> (7) Delete Account");
            System.out.println("> (0) Logout");
            System.out.println("-----------------------------");

            // Input handling
            try
            {
                System.out.print("[>>>Choice One]: ");
                choice = sc.nextInt();
                sc.nextLine();  // clear buffer
            }
            catch (Exception e)
            {
                System.out.println("Invalid Input! Please enter a number.");
                sc.nextLine();  // reset scanner
                continue;
            }

            // Menu selection
            switch (choice)
            {
                case 1: productManagement(); break;   // Manage products
                case 2: viewOrdersByProduct(); break; // View orders
                case 3: viewSalesReportAndTotalRevenue(); break; // Sales report
                case 4: viewCustomersByProduct(); break; // Customers
                case 5: wallet(); break; // Wallet operations
                case 6: editProfile(); break;
                case 7: deleteAccount();break;
                case 0:
                    System.out.println("Logging out of Seller Panel...");
                    return; // Exit loop
                default:
                    System.out.println("Invalid option. Try again.");
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
        System.out.println("PAN: " + this.pan);
        System.out.println("Wallet Balance: ₹" + this.walletAmount);
        System.out.println("-----------------------------");
    }

    // Product management menu for sellers
    void productManagement() {
        int choice;
        do {
            System.out.println("\n[PRODUCT_MANAGEMENT]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Add Product");
            System.out.println("> (2) View Products");
            System.out.println("> (3) Update Product");
            System.out.println("> (4) Delete Product");
            System.out.println("> (5) View Out-of-Stock Products"); // NEW
            System.out.println("> (6) Add/Remove Discount");       // NEW
            System.out.println("> (0) Back");
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
                case 1: addProduct(); break;
                case 2: viewAllProductsByCategory(); break;
                case 3: updateProductDetails(); break;
                case 4: deleteProduct(); break;
                case 5: viewOutOfStockProducts(); break; // NEW
                case 6: addDiscount(); break;            // NEW
                case 0: return;
                default: System.out.println("Invalid choice! Try again."); break;
            }
        } while (choice != 0);
    }


    // Wallet management menu for sellers
    public void wallet() {
        int choice = 0;

        while (true) {
            // Menu options
            System.out.println("\n[WALLET_MENU]");
            System.out.println("-----------------------------");
            System.out.println("> (1) View Wallet Balance");
            System.out.println("> (2) Add Money to Wallet");
            System.out.println("> (3) View All Transactions");
            System.out.println("> (4) Back to Seller Menu");
            System.out.println("-----------------------------");

            // Input handling
            try
            {
                System.out.print("[>>>Choice One]: ");
                choice = sc.nextInt();
                sc.nextLine(); // clear buffer
            }
            catch (Exception e)
            {
                System.out.println("Invalid Input! Please enter a number.");
                sc.nextLine(); // reset scanner
                continue;      // restart loop
            }

            // Menu selection
            switch (choice)
            {
                case 1: // View balance
                    double balance = this.getWalletAmount();
                    balance = Math.round(balance * 100) / 100.0;
                    System.out.println("Current Wallet Balance: ₹" + balance);
                    break;

                case 2: addMoneyToWallet(); break;           // Add money
                case 3: viewAllTransactions(); break;        // View transactions
                case 4: // Back to seller menu
                    System.out.println("Returning to Seller Menu...");
                    return;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    // Add a new product to seller's shop
    public void addProduct()
    {
        System.out.println("[ADD_PRODUCT_YOUR_SHOP]");
        System.out.println("-----------------------------");

        String[] categories = {
                "Sport", "Electronics", "Smart Home", "Stationery",
                "Footwear", "Fashion", "Toy Shop", "Grocery Store"
        };

        // --- Select Category ---
        for (int i = 0; i < categories.length; i++)
        {
            System.out.println("> (" + (i + 1) + ") " + categories[i]);
        }
        System.out.print("> Enter Category (1-" + categories.length + "): ");
        int choice;
        try { choice = Integer.parseInt(sc.nextLine()); }
        catch (Exception e) { choice = -1; }
        if (choice < 1 || choice > categories.length)
        {
            System.out.println("> Invalid category. Cancelled.");
            return;
        }
        String category = categories[choice - 1];

        // --- Product Details ---
        System.out.print("> Enter Product Name: ");
        String name = sc.nextLine();
        if (name.isEmpty())
        {
            System.out.println("> Product name cannot be empty.");
            return;
        }

        double originalPrice, mrp;
        try { System.out.print("> Enter Original Price: "); originalPrice = Double.parseDouble(sc.nextLine()); }
        catch (Exception e) { originalPrice = -1; }
        try { System.out.print("> Enter MRP: "); mrp = Double.parseDouble(sc.nextLine()); }
        catch (Exception e) { mrp = -1; }
        if (originalPrice <= 0 || mrp <= 0 || originalPrice > mrp)
        {
            System.out.println("> Invalid price values.");
            return;
        }

        int qty;
        try { System.out.print("> Enter Quantity: "); qty = Integer.parseInt(sc.nextLine()); }
        catch (Exception e) { qty = -1; }
        if (qty <= 0)
        {
            System.out.println("> Quantity must be greater than 0.");
            return;
        }

        double totalQtyPrice = originalPrice * qty;
        double totalProfit = (mrp - originalPrice) * qty;

        // --- Wallet Payment Check ---
        System.out.println("> Wallet Balance: " + walletAmount);
        System.out.println("> Total Pay: " + totalQtyPrice);
        System.out.print("> Enter Wallet Password: ");
        if (!sc.nextLine().equals(walletPassword))
        {
            System.out.println("> Wrong wallet password.");
            return;
        }
        if (walletAmount < totalQtyPrice)
        {
            System.out.println("> Insufficient wallet balance.");
            return;
        }

        // --- Database Insert ---
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert product with seller_id instead of seller_name
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO product (seller_id, seller_name, product_name, category, original_price, mrp, " +
                            "total_profit, quantity, total_quantity_price, discount_price) VALUES (?,?,?,?,?,?,?,?,?,?)")) {
                stmt.setInt(1, this.id);
                stmt.setString(2, this.name);
                stmt.setString(3, name);
                stmt.setString(4, category);
                stmt.setDouble(5, originalPrice);
                stmt.setDouble(6, mrp);
                stmt.setDouble(7, totalProfit);
                stmt.setInt(8, qty);
                stmt.setDouble(9, totalQtyPrice);
                stmt.setDouble(10, mrp);
                stmt.executeUpdate();
            }

            // Deduct from wallet
            try (PreparedStatement w = conn.prepareStatement(
                    "UPDATE seller SET wallet_amount = wallet_amount - ? WHERE seller_id = ?"))
            {
                w.setDouble(1, totalQtyPrice);
                w.setInt(2, this.id);
                w.executeUpdate();
            }

            // Record wallet transaction
            try (PreparedStatement t = conn.prepareStatement(
                    "INSERT INTO transaction (user_type, user_id, amount, transaction_type, payment_method, description) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"))
            {
                t.setString(1, "seller");
                t.setInt(2, this.id); // Use user_id instead of user_name
                t.setDouble(3, totalQtyPrice);
                t.setString(4, "debit");
                t.setString(5, "wallet");
                t.setString(6, "Product listing fee for " + name);
                t.executeUpdate();
            }

            conn.commit();
            walletAmount -= totalQtyPrice;

            System.out.println("> Product '" + name + "' added successfully!");
            System.out.println("> New Wallet Balance: " + walletAmount);

        }
        catch (SQLException e)
        {
            System.out.println("> Database Error: " + e.getMessage());
        }

        System.out.println("-----------------------------");
    }

    @Override
    public void viewAllProductsByCategory() {
        System.out.println("[VIEW_PRODUCTS_BY_CATEGORY]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT category FROM product WHERE seller_name=?")) {
            stmt.setString(1, this.name);
            ResultSet rs = stmt.executeQuery();

            // Use Stack to store categories
            Stack categoryStack = new Stack();
            while (rs.next()) {
                categoryStack.push(rs.getString("category"));
            }

            if (categoryStack.isEmpty()) {
                System.out.println("> No product categories found.");
                return;
            }

            // Display categories in reverse order (optional)
            System.out.println("Available Categories:");
            Stack tempStack = new Stack();
            while (!categoryStack.isEmpty()) {
                String category = (String) categoryStack.pop();
                tempStack.push(category);
                System.out.println("> " + category);
            }

            // Restore original stack
            while (!tempStack.isEmpty()) {
                categoryStack.push(tempStack.pop());
            }

            // Choose category
            System.out.print("> Choose a category: ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= categoryStack.size()) {
                    // Get the selected category
                    String selectedCategory = null;
                    for (int i = 0; i < choice; i++) {
                        if (i == choice - 1) {
                            selectedCategory = (String) categoryStack.pop();
                        } else {
                            tempStack.push(categoryStack.pop());
                        }
                    }
                    // Restore stack
                    while (!tempStack.isEmpty()) {
                        categoryStack.push(tempStack.pop());
                    }
                    categoryStack.push(selectedCategory);

                    if (selectedCategory != null) {
                        viewProductsBySelectedCategory(selectedCategory);
                    }
                } else {
                    System.out.println("> Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("> Please enter a valid number.");
            }

        } catch (SQLException e) {
            System.out.println("> Database Error: " + e.getMessage());
        }
        System.out.println("-----------------------------");
    }

    void viewProductsBySelectedCategory(String category) {
        System.out.println("[VIEW_PRODUCTS_IN_CATEGORY: " + category + "]");
        System.out.println("-----------------------------");

        // FIXED: Correct column names in SQL query
        String sql = "SELECT product_id, product_name, category, mrp, quantity, discount_price " +
                "FROM product WHERE seller_name=? AND category=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("> No products found in '" + category + "'.");
                return;
            }

            System.out.println("ID | Product Name | Category | MRP | Discount | Quantity");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("product_id") + " | " +          // FIXED: product_id instead of id
                                rs.getString("product_name") + " | " +
                                rs.getString("category") + " | Rs." +
                                rs.getDouble("mrp") + " | Rs." +
                                rs.getDouble("discount_price") + " | " +
                                rs.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            System.out.println("> Database Error: " + e.getMessage());
        }
        System.out.println("-----------------------------");
    }

    @Override
    public void updateProductDetails() {
        System.out.println("\n[UPDATE_PRODUCT_DETAILS]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Show all products of this seller
            String listSql = "SELECT product_id, product_name, category, mrp, quantity FROM product WHERE seller_name=?";
            PreparedStatement listStmt = conn.prepareStatement(listSql);
            listStmt.setString(1, this.name);
            ResultSet rs = listStmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("You have no products listed.");
                return;
            }

            System.out.println("\nYour Products:");
            System.out.println("ID | Name | Category | MRP | Qty");
            while (rs.next()) {
                System.out.println(rs.getInt("product_id") + " | " +
                        rs.getString("product_name") + " | " +
                        rs.getString("category") + " | Rs." +
                        rs.getDouble("mrp") + " | " +
                        rs.getInt("quantity"));
            }

            // 2. Select product
            System.out.print("\n[>>> Enter Product ID to update]: ");
            int pid = Integer.parseInt(sc.nextLine());

            String selectSql = "SELECT * FROM product WHERE product_id=? AND seller_name=?";
            PreparedStatement stmt = conn.prepareStatement(selectSql);
            stmt.setInt(1, pid);
            stmt.setString(2, this.name);
            ResultSet product = stmt.executeQuery();

            if (!product.next()) {
                System.out.println("Product not found or not yours.");
                return;
            }

            // 3. Update menu
            System.out.println("\n[UPDATE OPTIONS]");
            System.out.println("-----------------------------");
            System.out.println("> (1) Name");
            System.out.println("> (2) Category");
            System.out.println("> (3) Original Price");
            System.out.println("> (4) MRP");
            System.out.println("> (5) Quantity");
            System.out.println("-----------------------------");
            System.out.print("[>>> Choice one]: ");
            int choice = Integer.parseInt(sc.nextLine());

            String sql;
            PreparedStatement upd;
            int rows;

            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    sql = "UPDATE product SET product_name=? WHERE product_id=? AND seller_name=?";
                    upd = conn.prepareStatement(sql);
                    upd.setString(1, sc.nextLine());
                    upd.setInt(2, pid);
                    upd.setString(3, this.name);
                    rows = upd.executeUpdate();
                    System.out.println(rows > 0 ? "Name updated!" : "Update failed.");
                    break;

                case 2:
                    System.out.print("Enter new Category: ");
                    sql = "UPDATE product SET category=? WHERE product_id=? AND seller_name=?";
                    upd = conn.prepareStatement(sql);
                    upd.setString(1, sc.nextLine());
                    upd.setInt(2, pid);
                    upd.setString(3, this.name);
                    rows = upd.executeUpdate();
                    System.out.println(rows > 0 ? "Category updated!" : "Update failed.");
                    break;

                case 3:
                case 4:
                case 5:
                    double orig = product.getDouble("original_price");
                    double mrp  = product.getDouble("mrp");
                    int qty     = product.getInt("quantity");

                    if (choice == 3) {
                        System.out.print("Enter new Original Price: ");
                        orig = Double.parseDouble(sc.nextLine());
                    }
                    if (choice == 4) {
                        System.out.print("Enter new MRP: ");
                        mrp = Double.parseDouble(sc.nextLine());
                    }
                    if (choice == 5) {
                        System.out.print("Enter new Quantity: ");
                        qty = Integer.parseInt(sc.nextLine());
                    }

                    double profit = (mrp - orig) * qty;
                    double total  = mrp * qty;

                    String column = (choice == 3 ? "original_price" : choice == 4 ? "mrp" : "quantity");

                    sql = "UPDATE product SET " + column + "=?, total_profit=?, total_quantity_price=? WHERE product_id=? AND seller_name=?";
                    upd = conn.prepareStatement(sql);

                    if (choice == 3) upd.setDouble(1, orig);
                    if (choice == 4) upd.setDouble(1, mrp);
                    if (choice == 5) upd.setInt(1, qty);

                    upd.setDouble(2, profit);
                    upd.setDouble(3, total);
                    upd.setInt(4, pid);
                    upd.setString(5, this.name);

                    rows = upd.executeUpdate();
                    System.out.println(rows > 0 ? "Product updated!" : "Update failed.");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numbers correctly.");
        }
    }

    public void deleteProduct() {
        System.out.println("\n[DELETE_PRODUCT]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {

            // Show all products of this seller
            PreparedStatement listStmt = conn.prepareStatement(
                    "SELECT product_id, product_name, category, mrp, quantity FROM product WHERE seller_name=?"
            );
            listStmt.setString(1, this.name);
            ResultSet rs = listStmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("You have no products listed.");
                return;
            }

            System.out.println("\nYour Products:");
            System.out.println("ID | Name | Category | MRP | Qty");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " | " +
                        rs.getString(2) + " | " +
                        rs.getString(3) + " | Rs." +
                        rs.getDouble(4) + " | " +
                        rs.getInt(5));
            }

            // Ask for product to delete
            System.out.print("\n[>>> Enter Product ID to delete]: ");
            int productId = sc.nextInt();
            sc.nextLine();
            System.out.print("[>>> Confirm deletion (yes/no)]: ");
            String confirm = sc.nextLine().toLowerCase();

            if (confirm.equals("yes") || confirm.equals("y")) {
                PreparedStatement delStmt = conn.prepareStatement(
                        "DELETE FROM product WHERE product_id=? AND seller_name=?");
                delStmt.setInt(1, productId);
                delStmt.setString(2, this.name);

                int rows = delStmt.executeUpdate();
                System.out.println(rows > 0 ?
                        "Product deleted successfully." :
                        "Product not found or could not be deleted.");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine();
        }
    }

    public void viewOutOfStockProducts() {
        System.out.println("\n[VIEW_OUT_OF_STOCK_PRODUCTS]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT product_id, product_name, category, mrp, original_price " +
                            "FROM product WHERE seller_name = ? AND quantity = 0 ORDER BY product_name"
            );
            stmt.setString(1, this.name);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("> No out-of-stock products found.");
                return;
            }

            System.out.println("ID | Product Name | Category | MRP | Cost Price");
            System.out.println("-----------------------------------------------");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("product_id") + " | " +
                                rs.getString("product_name") + " | " +
                                rs.getString("category") + " | ₹" +
                                rs.getDouble("mrp") + " | ₹" +
                                rs.getDouble("original_price")
                );
            }

            System.out.println("-----------------------------------------------");

        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
        }
    }

    public void addDiscount() {
        System.out.println("\n[ADD_DISCOUNT_TO_PRODUCT]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            // Show all products
            PreparedStatement listStmt = conn.prepareStatement(
                    "SELECT product_id, product_name, mrp, discount_price, quantity " +
                            "FROM product WHERE seller_name = ? ORDER BY product_name"
            );
            listStmt.setString(1, this.name);
            ResultSet rs = listStmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("> No products found.");
                return;
            }

            System.out.println("Your Products:");
            System.out.println("ID | Name | MRP | Current Discount | Stock");
            System.out.println("------------------------------------------");

            while (rs.next()) {
                double currentDiscount = rs.getDouble("discount_price");
                double mrp = rs.getDouble("mrp");
                String discountDisplay = (currentDiscount < mrp) ? "₹" + currentDiscount : "No discount";

                System.out.println(
                        rs.getInt("product_id") + " | " +
                                rs.getString("product_name") + " | ₹" +
                                mrp + " | " +
                                discountDisplay + " | " +
                                rs.getInt("quantity")
                );
            }
            System.out.println("------------------------------------------");

            // Select product
            System.out.print("[>>> Enter Product ID to add discount]: ");
            int productId = sc.nextInt();
            sc.nextLine();

            // Get current product details
            PreparedStatement productStmt = conn.prepareStatement(
                    "SELECT product_name, mrp, discount_price FROM product " +
                            "WHERE product_id = ? AND seller_name = ?"
            );
            productStmt.setInt(1, productId);
            productStmt.setString(2, this.name);
            ResultSet productRs = productStmt.executeQuery();

            if (!productRs.next()) {
                System.out.println("> Product not found or doesn't belong to you.");
                return;
            }

            String productName = productRs.getString("product_name");
            double mrp = productRs.getDouble("mrp");
            double currentPrice = productRs.getDouble("discount_price");

            System.out.println("Product: " + productName);
            System.out.println("MRP: ₹" + mrp);
            System.out.println("Current Price: ₹" + currentPrice);

            // Get discount details
            System.out.print("[>>> Enter discount percentage (0-100) or 0 to remove discount]: ");
            double discountPercent = sc.nextDouble();
            sc.nextLine();

            if (discountPercent < 0 || discountPercent > 100) {
                System.out.println("> Invalid discount percentage.");
                return;
            }

            double newPrice;
            if (discountPercent == 0) {
                newPrice = mrp; // Remove discount
                System.out.println("> Removing discount from product...");
            } else {
                newPrice = mrp * (1 - discountPercent / 100);
                newPrice = Math.round(newPrice * 100.0) / 100.0; // Round to 2 decimals
                System.out.println("> New price after " + discountPercent + "% discount: ₹" + newPrice);
            }

            // Confirm
            System.out.print("[>>> Confirm price change (yes/no)]: ");
            String confirm = sc.nextLine().toLowerCase();

            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("> Discount operation cancelled.");
                return;
            }

            // Update database
            PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE product SET discount_price = ? WHERE product_id = ? AND seller_name = ?"
            );
            updateStmt.setDouble(1, newPrice);
            updateStmt.setInt(2, productId);
            updateStmt.setString(3, this.name);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                if (discountPercent == 0) {
                    System.out.println("> Discount removed successfully!");
                } else {
                    System.out.println("> Discount applied successfully! New price: ₹" + newPrice);
                }
            } else {
                System.out.println("> Failed to update product price.");
            }

        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
            sc.nextLine();
        }
    }

    // In seller.java - Fix the viewOrdersByProduct method
    public void viewOrdersByProduct() {
        System.out.println("\n[VIEW_ORDERS_BY_PRODUCT]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {

            // Get seller's products - FIXED column names
            PreparedStatement productStmt = conn.prepareStatement(
                    "SELECT product_id, product_name FROM product WHERE seller_name = ?");
            productStmt.setString(1, this.name);
            ResultSet productRs = productStmt.executeQuery();

            if (!productRs.isBeforeFirst()) {
                System.out.println("> No products found.");
                return;
            }

            System.out.println("Your Products:");
            while (productRs.next()) {
                System.out.println("ID: " + productRs.getInt("product_id") + " - " +
                        productRs.getString("product_name"));
            }

            System.out.print("> Enter Product ID: ");
            int productId = sc.nextInt();
            sc.nextLine();

            // Get orders for this product - FIXED: Simplified query with correct column names
            PreparedStatement orderStmt = conn.prepareStatement(
                    "SELECT order_no, customer_name, quantity, total_price, order_date " +
                            "FROM orders WHERE seller_name = ? AND product_id = ?");
            orderStmt.setString(1, this.name);
            orderStmt.setInt(2, productId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.isBeforeFirst()) {
                System.out.println("> No orders found for this product.");
                return;
            }

            System.out.println("Orders for Product ID " + productId + ":");
            System.out.println("Order No | Customer | Quantity | Total Price | Order Date");
            System.out.println("---------------------------------------------------------");
            while (orderRs.next()) {
                System.out.println(
                        orderRs.getInt("order_no") + " | " +
                                orderRs.getString("customer_name") + " | " +
                                orderRs.getInt("quantity") + " | ₹" +
                                orderRs.getDouble("total_price") + " | " +
                                orderRs.getTimestamp("order_date")
                );
            }

        } catch (SQLException e) {
            System.out.println("> Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

     void viewSalesReportAndTotalRevenue() {
        System.out.println("\n[SALES_REPORT_AND_REVENUE]");
        System.out.println("-----------------------------");

        try {
            Connection conn = DBConnection.getConnection();

            // ---- Get Total Revenue ----
            String revenueQuery = "SELECT SUM(total_price) AS total_revenue " +
                    "FROM orders WHERE seller_name = ?";
            PreparedStatement revenueStmt = conn.prepareStatement(revenueQuery);
            revenueStmt.setString(1, this.name);
            ResultSet revenueRs = revenueStmt.executeQuery();

            double totalRevenue = 0.0;
            if (revenueRs.next()) {
                totalRevenue = revenueRs.getDouble("total_revenue");
            }

            // ---- Get Total Profit ----
            String profitQuery = "SELECT SUM((p.mrp - p.original_price) * o.quantity) AS total_profit " +
                    "FROM orders o " +
                    "JOIN product p ON o.product_name = p.product_name " +
                    "AND o.seller_name = p.seller_name " +
                    "WHERE o.seller_name = ?";
            PreparedStatement profitStmt = conn.prepareStatement(profitQuery);
            profitStmt.setString(1, this.name);
            ResultSet profitRs = profitStmt.executeQuery();

            double totalProfit = 0.0;
            if (profitRs.next()) {
                totalProfit = profitRs.getDouble("total_profit");
            }

            // ---- Round to 2 decimals ----
            totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
            totalProfit = Math.round(totalProfit * 100.0) / 100.0;

            // ---- Display ----
            System.out.println("> Total Revenue: ₹" + totalRevenue);
            System.out.println("> Total Profit:  ₹" + totalProfit);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void editProfile() {
        System.out.println("\n[EDIT_PROFILE]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            // Fetch current seller details
            PreparedStatement getStmt = conn.prepareStatement(
                    "SELECT seller_name, email, phone, pan FROM seller WHERE seller_id=?");
            getStmt.setInt(1, this.id);
            ResultSet rs = getStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Seller not found.");
                return;
            }

            // Show current details
            System.out.println("> 1. Name:  " + rs.getString("seller_name"));
            System.out.println("> 2. Email: " + rs.getString("email"));
            System.out.println("> 3. Phone: " + rs.getString("phone"));
            System.out.println("> 4. PAN:   " + rs.getString("pan"));
            System.out.println();

            System.out.print("[>>> Enter field number to edit (0 to cancel)]: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("Edit cancelled.");
                return;
            }

            String field = "";
            String value = "";
            String updateQuery = "";

            switch (choice) {
                case 1:
                    field = "seller_name";
                    do {
                        System.out.print("[>>> Enter new name]: ");
                        value = sc.nextLine();
                    } while (value.isEmpty());
                    updateQuery = "UPDATE seller SET seller_name = ? WHERE seller_id = ?";
                    break;

                case 2:
                    field = "email";
                    do {
                        System.out.print("[>>> Enter new email]: ");
                        value = sc.nextLine();
                    } while (!validator.isValidEmail(value));
                    updateQuery = "UPDATE seller SET email = ? WHERE seller_id = ?";
                    break;

                case 3:
                    field = "phone";
                    do {
                        System.out.print("[>>> Enter new phone]: ");
                        value = sc.nextLine();
                    } while (!validator.isValidPhoneNumber(value));
                    updateQuery = "UPDATE seller SET phone = ? WHERE seller_id = ?";
                    break;

                case 4:
                    field = "pan";
                    do {
                        System.out.print("[>>> Enter new PAN]: ");
                        value = sc.nextLine();
                    } while (!validator.isValidPan(value));
                    updateQuery = "UPDATE seller SET pan = ? WHERE seller_id = ?";
                    break;

                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            // Use parameterized query to prevent SQL injection
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, value);
            updateStmt.setInt(2, this.id);

            int rows = updateStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Profile updated successfully!");

                // Update local object
                if (choice == 1) name = value;
                if (choice == 2) email = value;
                if (choice == 3) phone = value;
                if (choice == 4) pan = value;

            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("email"))
                    System.out.println("Email already exists.");
                else if (e.getMessage().contains("pan"))
                    System.out.println("PAN already exists.");
            } else {
                System.out.println("Database error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method to add money to the wallet
    public void addMoneyToWallet() {
        System.out.println("\n[ADD_MONEY_TO_WALLET]");
        System.out.println("-----------------------------");
        System.out.println("> 1. UPI");
        System.out.println("> 2. Net Banking");
        System.out.println("> 3. Card");
        System.out.println("> 4. Gift Card");
        System.out.print("[>>> Choose payment method]: ");
        int choice = sc.nextInt();
        sc.nextLine();

        System.out.print("[>>> Enter amount to add]: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        String method = "", note = "";

        switch (choice) {
            case 1:
                System.out.print("[>>> Enter UPI ID]: ");
                String upi = sc.nextLine();
                method = "UPI";
                note = "Money added using UPI (" + upi + ")";
                break;

            case 2:
                System.out.print("[>>> Enter Bank Name]: ");
                String bank = sc.nextLine();
                System.out.print("[>>> Enter Account No (last 4 digits)]: ");
                String acc = sc.nextLine();
                method = "Net Banking";
                note = "Money added using NetBanking (" + bank + " XXXX" + acc + ")";
                break;

            case 3:
                System.out.print("[>>> Enter Card Number]: ");
                String card = sc.nextLine();
                method = "Card";
                note = "Money added using Card ****" + card.substring(card.length() - 4);
                break;

            case 4:
                System.out.print("[>>> Enter Gift Card Code]: ");
                String gift = sc.nextLine();
                method = "Gift Card";
                note = "Gift Card Redeemed (" + gift + ")";
                break;

            default:
                System.out.println("Invalid payment method.");
                return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Update wallet balance - FIXED column name: seller_id
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE seller SET wallet_amount = wallet_amount + ? WHERE seller_id = ?");
            update.setDouble(1, amount);
            update.setInt(2, this.id);
            update.executeUpdate();

            // Save transaction record - FIXED: using user_id instead of user_name
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO transaction(user_type, user_id, amount, transaction_type, payment_method, description) VALUES(?, ?, ?, ?, ?, ?)");
            insert.setString(1, "seller");
            insert.setInt(2, this.id); // Use user_id (INT) instead of user_name (VARCHAR)
            insert.setDouble(3, amount);
            insert.setString(4, "credit");
            insert.setString(5, method);
            insert.setString(6, note);
            insert.executeUpdate();

            conn.commit();

            this.walletAmount += amount;
            System.out.println("Successfully added ₹" + amount);
            System.out.println("New Wallet Balance: ₹" + this.walletAmount);

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    public void viewAllTransactions() {
        System.out.println("\n[ALL_WALLET_TRANSACTIONS]");
        System.out.println("-----------------------------");

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT transaction_date, amount, transaction_type, payment_method, description " +
                    "FROM transaction WHERE user_id = ? AND user_type = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.id);
            stmt.setString(2, "seller");
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No transactions found.");
            } else {
                System.out.println("\nDate | Amount | Type | Method | Description");
                System.out.println("-----------------------------");

                while (rs.next()) {
                    Timestamp date = rs.getTimestamp("transaction_date");
                    double amount = Math.round(rs.getDouble("amount") * 100.0) / 100.0;
                    String type = rs.getString("transaction_type");
                    String method = rs.getString("payment_method");
                    String description = rs.getString("description");

                    System.out.println("> " + date + " | Rs." + amount + " | " + type + " | " + method + " | " + description);
                }

                System.out.println("-----------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void viewCustomersByProduct() {
        System.out.println("\n[VIEW_CUSTOMERS_BY_PRODUCT]");
        System.out.println("-----------------------------");

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Show seller's products
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT product_id, product_name FROM product WHERE seller_name = ?"
            );
            ps.setString(1, this.name);
            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("You have no products listed yet.");
                return;
            }

            System.out.println("Your Products:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("product_id") + " | Name: " +
                        rs.getString("product_name"));
            }

            // 2. Select product
            System.out.print("[>>> Enter Product ID]: ");
            int pid = sc.nextInt();
            sc.nextLine();

            // Verify product belongs to seller
            ps = conn.prepareStatement(
                    "SELECT product_name FROM product WHERE product_id = ? AND seller_name = ?");
            ps.setInt(1, pid);
            ps.setString(2, this.name);
            rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Product not found or doesn't belong to you.");
                return;
            }

            String pname = rs.getString("product_name");

            // 3. Fetch customers who ordered that product
            ps = conn.prepareStatement(
                    "SELECT DISTINCT c.customer_name, c.email, c.phone, c.address " +
                            "FROM customer c JOIN orders o ON c.customer_name = o.customer_name " +
                            "WHERE o.seller_name = ? AND o.product_id = ?");
            ps.setString(1, this.name);
            ps.setInt(2, pid);
            rs = ps.executeQuery();

            // Use Stack to store customer data (LIFO - most recent first)
            Stack customerStack = new Stack();
            while (rs.next()) {
                String customerData = rs.getString("customer_name") + " | " +
                        rs.getString("email") + " | " +
                        rs.getString("phone") + " | " +
                        rs.getString("address");
                customerStack.push(customerData);
            }

            // 4. Display results (LIFO order - most recent first)
            if (customerStack.isEmpty()) {
                System.out.println("No customers found for the selected product.");
            } else {
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Customer Name | Email | Phone | Address (Most recent first)");
                System.out.println("--------------------------------------------------------------------------------");

                // Create temporary stack to reverse order for display (optional)
                Stack tempStack = new Stack();
                while (!customerStack.isEmpty()) {
                    tempStack.push(customerStack.pop());
                }

                while (!tempStack.isEmpty()) {
                    System.out.println(tempStack.pop());
                }
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("Total customers: " + customerStack.size());
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void deleteAccount() {
        User.deleteAccount("Seller", "seller", this.id, this.name, this.password);
    }

}