-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 23, 2025 at 08:51 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ecommerce`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `RegisterCustomer` (IN `p_customer_name` VARCHAR(100), IN `p_email` VARCHAR(100), IN `p_phone` VARCHAR(15), IN `p_address` VARCHAR(255), IN `p_password` VARCHAR(100), IN `p_wallet_password` VARCHAR(100))   BEGIN
    INSERT INTO customer (customer_name, email, phone, address, password, wallet_password, wallet_amount)
    VALUES (p_customer_name, p_email, p_phone, p_address, p_password, p_wallet_password, 0.0);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `RegisterSeller` (IN `p_seller_name` VARCHAR(100), IN `p_email` VARCHAR(100), IN `p_phone` VARCHAR(15), IN `p_pan` VARCHAR(20), IN `p_password` VARCHAR(100), IN `p_wallet_password` VARCHAR(100))   BEGIN
    INSERT INTO seller (seller_name, email, phone, pan, password, wallet_password, wallet_amount)
    VALUES (p_seller_name, p_email, p_phone, p_pan, p_password, p_wallet_password, 0.0);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `password` varchar(100) NOT NULL,
  `wallet_password` varchar(100) DEFAULT NULL,
  `wallet_amount` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `customer_name`, `email`, `phone`, `address`, `password`, `wallet_password`, `wallet_amount`) VALUES
(1, 'Rajesh Kumar', 'rajesh.kumar@email.com', '9876543210', '123 Main St, Mumbai', 'Pass@123', '1234', 5000),
(2, 'Priya Sharma', 'priya.sharma@email.com', '8765432109', '456 Park Ave, Delhi', 'Pass@123', '2345', 7500),
(3, 'Amit Patel', 'amit.patel@email.com', '7654321098', '789 Gandhi Rd, Bangalore', 'Pass@123', '3456', 3000),
(4, 'Sneha Singh', 'sneha.singh@email.com', '6543210987', '321 MG Road, Pune', 'Pass@123', '4567', 4200),
(5, 'Vikram Malhotra', 'vikram.m@email.com', '9432109876', '654 Cross Rd, Chennai', 'Pass@123', '5678', 6800),
(6, 'Anjali Desai', 'anjali.desai@email.com', '8321098765', '987 Church St, Hyderabad', 'Pass@123', '6789', 2500),
(7, 'Rahul Joshi', 'rahul.joshi@email.com', '7210987654', '555 Temple Rd, Kolkata', 'Pass@123', '7890', 5300),
(8, 'Neha Gupta', 'neha.gupta@email.com', '6109876543', '222 Market St, Ahmedabad', 'Pass@123', '8901', 3700),
(9, 'Sanjay Verma', 'sanjay.verma@email.com', '5098765432', '444 Mall Rd, Jaipur', 'Pass@123', '9012', 6100),
(10, 'Kavita Reddy', 'kavita.reddy@email.com', '4987654321', '777 Hill Rd, Kochi', 'Pass@123', '0123', 4900);

--
-- Triggers `customer`
--
DELIMITER $$
CREATE TRIGGER `after_customer_insert_log` AFTER INSERT ON `customer` FOR EACH ROW BEGIN
    INSERT INTO wallet_transaction (user_type, user_id, amount, transaction_type, payment_method, description)
    VALUES ('customer', NEW.customer_id, 0.0, 'credit', 'system', 'Initial wallet setup on registration');
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_no` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `seller_id` int(11) NOT NULL,
  `seller_name` varchar(100) NOT NULL,
  `product_id` int(11) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `payment_status` varchar(20) DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `order_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_no`, `customer_id`, `customer_name`, `seller_id`, `seller_name`, `product_id`, `product_name`, `quantity`, `total_price`, `payment_status`, `payment_method`, `order_date`) VALUES
(1, 1, 'Rajesh Kumar', 1, 'ElectroWorld', 1, 'Smartphone X', 1, 18999, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(2, 2, 'Priya Sharma', 2, 'FashionHub', 6, 'Men\'s Formal Shirt', 2, 2598, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(3, 3, 'Amit Patel', 3, 'SportsGear', 11, 'Cricket Bat', 1, 2199, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(4, 4, 'Sneha Singh', 4, 'HomeEssentials', 16, 'Smart Bulb', 3, 2397, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(5, 5, 'Vikram Malhotra', 5, 'BookParadise', 21, 'Notebook Set', 5, 1745, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(6, 6, 'Anjali Desai', 6, 'ToyLand', 26, 'Lego Set', 1, 1799, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(7, 7, 'Rahul Joshi', 7, 'GroceryMart', 31, 'Organic Rice', 10, 1000, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(8, 8, 'Neha Gupta', 2, 'FashionHub', 36, 'Sports Shoes', 1, 2599, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(9, 9, 'Sanjay Verma', 1, 'ElectroWorld', 2, 'Wireless Earbuds', 2, 4998, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(10, 10, 'Kavita Reddy', 3, 'SportsGear', 13, 'Yoga Mat', 1, 899, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(11, 1, 'Rajesh Kumar', 4, 'HomeEssentials', 18, 'Smart Lock', 1, 5499, 'pending', 'Cash on Delivery', '2025-08-23 06:38:19'),
(12, 2, 'Priya Sharma', 5, 'BookParadise', 23, 'Art Supplies Kit', 1, 1299, 'pending', 'Cash on Delivery', '2025-08-23 06:38:19'),
(13, 3, 'Amit Patel', 6, 'ToyLand', 28, 'Educational Toy', 2, 1798, 'pending', 'Cash on Delivery', '2025-08-23 06:38:19'),
(14, 4, 'Sneha Singh', 7, 'GroceryMart', 33, 'Organic Honey', 3, 1347, 'pending', 'Cash on Delivery', '2025-08-23 06:38:19'),
(15, 5, 'Vikram Malhotra', 2, 'FashionHub', 8, 'Women\'s Dress', 1, 1799, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(16, 6, 'Anjali Desai', 1, 'ElectroWorld', 3, '4K Smart TV', 1, 44999, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(17, 7, 'Rahul Joshi', 3, 'SportsGear', 14, 'Running Shoes', 1, 2999, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(18, 8, 'Neha Gupta', 4, 'HomeEssentials', 19, 'Wi-Fi Router', 1, 3499, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(19, 9, 'Sanjay Verma', 5, 'BookParadise', 24, 'Desk Organizer', 2, 1598, 'completed', 'Wallet', '2025-08-23 06:38:19'),
(20, 10, 'Kavita Reddy', 6, 'ToyLand', 29, 'Board Game', 1, 799, 'completed', 'Wallet', '2025-08-23 06:38:19');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `product_id` int(11) NOT NULL,
  `seller_id` int(11) NOT NULL,
  `seller_name` varchar(100) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `original_price` double NOT NULL,
  `mrp` double NOT NULL,
  `total_profit` double DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `total_quantity_price` double DEFAULT NULL,
  `discount_price` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`product_id`, `seller_id`, `seller_name`, `product_name`, `category`, `original_price`, `mrp`, `total_profit`, `quantity`, `total_quantity_price`, `discount_price`) VALUES
(1, 1, 'ElectroWorld', 'Smartphone X', 'Electronics', 15000, 19999, 4999, 50, 750000, 18999),
(2, 1, 'ElectroWorld', 'Wireless Earbuds', 'Electronics', 2000, 2999, 999, 100, 200000, 2499),
(3, 1, 'ElectroWorld', '4K Smart TV', 'Electronics', 35000, 49999, 14999, 20, 700000, 44999),
(4, 1, 'ElectroWorld', 'Laptop Pro', 'Electronics', 55000, 74999, 19999, 15, 825000, 69999),
(5, 1, 'ElectroWorld', 'Smart Watch', 'Electronics', 8000, 12999, 4999, 40, 320000, 11999),
(6, 2, 'FashionHub', 'Men\'s Formal Shirt', 'Fashion', 800, 1499, 699, 100, 80000, 1299),
(7, 2, 'FashionHub', 'Women\'s Dress', 'Fashion', 1200, 1999, 799, 80, 96000, 1799),
(8, 2, 'FashionHub', 'Jeans', 'Fashion', 900, 1599, 699, 120, 108000, 1399),
(9, 2, 'FashionHub', 'Winter Jacket', 'Fashion', 2000, 3499, 1499, 50, 100000, 2999),
(10, 2, 'FashionHub', 'Casual T-Shirt', 'Fashion', 400, 799, 399, 150, 60000, 699),
(11, 3, 'SportsGear', 'Cricket Bat', 'Sport', 1500, 2499, 999, 40, 60000, 2199),
(12, 3, 'SportsGear', 'Football', 'Sport', 800, 1499, 699, 60, 48000, 1299),
(13, 3, 'SportsGear', 'Yoga Mat', 'Sport', 600, 999, 399, 80, 48000, 899),
(14, 3, 'SportsGear', 'Running Shoes', 'Sport', 2000, 3499, 1499, 30, 60000, 2999),
(15, 3, 'SportsGear', 'Tennis Racket', 'Sport', 2500, 3999, 1499, 25, 62500, 3499),
(16, 4, 'HomeEssentials', 'Smart Bulb', 'Smart Home', 500, 899, 399, 100, 50000, 799),
(17, 4, 'HomeEssentials', 'Robot Vacuum', 'Smart Home', 12000, 17999, 5999, 15, 180000, 15999),
(18, 4, 'HomeEssentials', 'Smart Lock', 'Smart Home', 4000, 5999, 1999, 25, 100000, 5499),
(19, 4, 'HomeEssentials', 'Wi-Fi Router', 'Smart Home', 2500, 3999, 1499, 30, 75000, 3499),
(20, 4, 'HomeEssentials', 'Smart Plug', 'Smart Home', 700, 1299, 599, 50, 35000, 1099),
(21, 5, 'BookParadise', 'Notebook Set', 'Stationery', 200, 399, 199, 200, 40000, 349),
(22, 5, 'BookParadise', 'Pen Set', 'Stationery', 150, 299, 149, 300, 45000, 249),
(23, 5, 'BookParadise', 'Art Supplies Kit', 'Stationery', 800, 1499, 699, 50, 40000, 1299),
(24, 5, 'BookParadise', 'Desk Organizer', 'Stationery', 500, 899, 399, 70, 35000, 799),
(25, 5, 'BookParadise', 'Planner', 'Stationery', 300, 599, 299, 100, 30000, 499),
(26, 6, 'ToyLand', 'Lego Set', 'Toy Shop', 1200, 1999, 799, 40, 48000, 1799),
(27, 6, 'ToyLand', 'Remote Car', 'Toy Shop', 800, 1499, 699, 60, 48000, 1299),
(28, 6, 'ToyLand', 'Educational Toy', 'Toy Shop', 600, 999, 399, 80, 48000, 899),
(29, 6, 'ToyLand', 'Board Game', 'Toy Shop', 500, 899, 399, 70, 35000, 799),
(30, 6, 'ToyLand', 'Doll House', 'Toy Shop', 1500, 2499, 999, 25, 37500, 2199),
(31, 7, 'GroceryMart', 'Organic Rice', 'Grocery Store', 80, 120, 40, 500, 40000, 100),
(32, 7, 'GroceryMart', 'Extra Virgin Olive Oil', 'Grocery Store', 400, 599, 199, 200, 80000, 549),
(33, 7, 'GroceryMart', 'Organic Honey', 'Grocery Store', 300, 499, 199, 150, 45000, 449),
(34, 7, 'GroceryMart', 'Spice Box Set', 'Grocery Store', 600, 999, 399, 100, 60000, 899),
(35, 7, 'GroceryMart', 'Tea Assortment', 'Grocery Store', 250, 399, 149, 300, 75000, 349),
(36, 2, 'FashionHub', 'Sports Shoes', 'Footwear', 1800, 2999, 1199, 60, 108000, 2599),
(37, 2, 'FashionHub', 'Formal Shoes', 'Footwear', 1500, 2499, 999, 50, 75000, 2199),
(38, 2, 'FashionHub', 'Casual Sandals', 'Footwear', 800, 1499, 699, 80, 64000, 1299),
(39, 2, 'FashionHub', 'Winter Boots', 'Footwear', 2200, 3799, 1599, 40, 88000, 3299),
(40, 2, 'FashionHub', 'Sneakers', 'Footwear', 1200, 1999, 799, 70, 84000, 1799);

-- --------------------------------------------------------

--
-- Table structure for table `seller`
--

CREATE TABLE `seller` (
  `seller_id` int(11) NOT NULL,
  `seller_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `pan` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL,
  `wallet_password` varchar(100) DEFAULT NULL,
  `wallet_amount` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seller`
--

INSERT INTO `seller` (`seller_id`, `seller_name`, `email`, `phone`, `pan`, `password`, `wallet_password`, `wallet_amount`) VALUES
(1, 'ElectroWorld', 'electroworld@email.com', '9123456780', 'ABCDF1234E', 'Seller@123', '1111', 25000),
(2, 'FashionHub', 'fashionhub@email.com', '8234567891', 'BCDEF2345F', 'Seller@123', '2222', 18000),
(3, 'SportsGear', 'sportsgear@email.com', '7345678902', 'CDEFG3456G', 'Seller@123', '3333', 32000),
(4, 'HomeEssentials', 'homeessentials@email.com', '6456789013', 'DEFGH4567H', 'Seller@123', '4444', 21000),
(5, 'BookParadise', 'bookparadise@email.com', '5567890124', 'EFGHI5678I', 'Seller@123', '5555', 15000),
(6, 'ToyLand', 'toyland@email.com', '4678901235', 'FGHIJ6789J', 'Seller@123', '6666', 28000),
(7, 'GroceryMart', 'grocerymart@email.com', '3789012346', 'GHIJK7890K', 'Seller@123', '7777', 35000),
(8, 'TechGadgets', 'techgadgets@email.com', '2890123457', 'HIJKL8901L', 'Seller@123', '8888', 42000),
(9, 'BeautyBliss', 'beautybliss@email.com', '1901234568', 'IJKLM9012M', 'Seller@123', '9999', 19000),
(10, 'FurnitureWorld', 'furnitureworld@email.com', '1012345679', 'JKLMN0123N', 'Seller@123', '0000', 50000);

--
-- Triggers `seller`
--
DELIMITER $$
CREATE TRIGGER `after_seller_insert_log` AFTER INSERT ON `seller` FOR EACH ROW BEGIN
    INSERT INTO wallet_transaction (user_type, user_id, amount, transaction_type, payment_method, description)
    VALUES ('seller', NEW.seller_id, 0.0, 'credit', 'system', 'Initial wallet setup on registration');
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transaction_no` int(11) NOT NULL,
  `user_type` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `transaction_type` varchar(10) NOT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transaction_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transaction_no`, `user_type`, `user_id`, `amount`, `transaction_type`, `payment_method`, `description`, `transaction_date`) VALUES
(1, 'customer', 1, 10000, 'credit', 'UPI', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(2, 'customer', 1, 18999, 'debit', 'Wallet', 'Order #1 for Smartphone X', '2025-08-23 06:38:19'),
(3, 'customer', 2, 8000, 'credit', 'Net Banking', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(4, 'customer', 2, 2598, 'debit', 'Wallet', 'Order #2 for Men\'s Formal Shirt', '2025-08-23 06:38:19'),
(5, 'customer', 3, 5000, 'credit', 'Card', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(6, 'customer', 3, 2199, 'debit', 'Wallet', 'Order #3 for Cricket Bat', '2025-08-23 06:38:19'),
(7, 'customer', 4, 5000, 'credit', 'UPI', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(8, 'customer', 4, 2397, 'debit', 'Wallet', 'Order #4 for Smart Bulb', '2025-08-23 06:38:19'),
(9, 'customer', 5, 7000, 'credit', 'Net Banking', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(10, 'customer', 5, 1745, 'debit', 'Wallet', 'Order #5 for Notebook Set', '2025-08-23 06:38:19'),
(11, 'customer', 6, 3000, 'credit', 'Card', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(12, 'customer', 6, 1799, 'debit', 'Wallet', 'Order #6 for Lego Set', '2025-08-23 06:38:19'),
(13, 'customer', 7, 6000, 'credit', 'UPI', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(14, 'customer', 7, 1000, 'debit', 'Wallet', 'Order #7 for Organic Rice', '2025-08-23 06:38:19'),
(15, 'customer', 8, 4000, 'credit', 'Net Banking', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(16, 'customer', 8, 2599, 'debit', 'Wallet', 'Order #8 for Sports Shoes', '2025-08-23 06:38:19'),
(17, 'customer', 9, 7000, 'credit', 'Card', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(18, 'customer', 9, 4998, 'debit', 'Wallet', 'Order #9 for Wireless Earbuds', '2025-08-23 06:38:19'),
(19, 'customer', 10, 5000, 'credit', 'UPI', 'Initial wallet top-up', '2025-08-23 06:38:19'),
(20, 'customer', 10, 899, 'debit', 'Wallet', 'Order #10 for Yoga Mat', '2025-08-23 06:38:19'),
(21, 'seller', 1, 25000, 'credit', 'Bank Transfer', 'Initial business funding', '2025-08-23 06:38:19'),
(22, 'seller', 1, 750000, 'debit', 'wallet', 'Product listing fee for Smartphone X', '2025-08-23 06:38:19'),
(23, 'seller', 1, 200000, 'debit', 'wallet', 'Product listing fee for Wireless Earbuds', '2025-08-23 06:38:19'),
(24, 'seller', 1, 700000, 'debit', 'wallet', 'Product listing fee for 4K Smart TV', '2025-08-23 06:38:19'),
(25, 'seller', 1, 825000, 'debit', 'wallet', 'Product listing fee for Laptop Pro', '2025-08-23 06:38:19'),
(26, 'seller', 1, 320000, 'debit', 'wallet', 'Product listing fee for Smart Watch', '2025-08-23 06:38:19'),
(27, 'seller', 2, 18000, 'credit', 'Bank Transfer', 'Initial business funding', '2025-08-23 06:38:19'),
(28, 'seller', 2, 80000, 'debit', 'wallet', 'Product listing fee for Men\'s Formal Shirt', '2025-08-23 06:38:19'),
(29, 'seller', 2, 96000, 'debit', 'wallet', 'Product listing fee for Women\'s Dress', '2025-08-23 06:38:19'),
(30, 'seller', 2, 108000, 'debit', 'wallet', 'Product listing fee for Jeans', '2025-08-23 06:38:19'),
(31, 'seller', 2, 100000, 'debit', 'wallet', 'Product listing fee for Winter Jacket', '2025-08-23 06:38:19'),
(32, 'seller', 2, 60000, 'debit', 'wallet', 'Product listing fee for Casual T-Shirt', '2025-08-23 06:38:19'),
(33, 'seller', 2, 108000, 'debit', 'wallet', 'Product listing fee for Sports Shoes', '2025-08-23 06:38:19'),
(34, 'seller', 2, 75000, 'debit', 'wallet', 'Product listing fee for Formal Shoes', '2025-08-23 06:38:19'),
(35, 'seller', 2, 64000, 'debit', 'wallet', 'Product listing fee for Casual Sandals', '2025-08-23 06:38:19'),
(36, 'seller', 2, 88000, 'debit', 'wallet', 'Product listing fee for Winter Boots', '2025-08-23 06:38:19'),
(37, 'seller', 2, 84000, 'debit', 'wallet', 'Product listing fee for Sneakers', '2025-08-23 06:38:19');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_no`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `seller_id` (`seller_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `seller_id` (`seller_id`);

--
-- Indexes for table `seller`
--
ALTER TABLE `seller`
  ADD PRIMARY KEY (`seller_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `pan` (`pan`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transaction_no`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_no` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `product`
--
ALTER TABLE `product`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `seller`
--
ALTER TABLE `seller`
  MODIFY `seller_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transaction_no` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`seller_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE;

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`seller_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
