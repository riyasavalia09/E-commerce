## E-Commerce Management System

A Java-based E-Commerce Management System designed to manage customers, sellers, products, carts, orders, billing, and database operations. The project demonstrates the practical use of Java, MySQL, Object-Oriented Programming, Data Structures, and DBMS concepts in an e-commerce environment.

---

## Project Overview

The E-Commerce Management System provides a structured platform for handling different operations of an online shopping system.

The project includes separate roles and modules for customers, sellers, and administrators, along with database connectivity and custom data structures.

It was developed as an academic project to apply programming, database management, and data structure concepts to a real-world application.

---

## Features

## Customer

- Customer registration and login
- Product browsing
- Product selection
- Add products to cart
- Cart management
- Order placement
- Billing and order details
- Customer validation

## Seller

- Seller registration and login
- Seller-related product management
- Product and order handling

## Admin

- Administrative operations
- User and system management
- Database-related operations

## Cart & Orders

- Add and manage cart items
- Order processing
- Billing system
- Customer order records

## Validation & Security

- User validation
- OTP-related functionality
- Input validation
- Role-based operations

## Database

- MySQL database integration
- Database connection using JDBC
- SQL tables and database operations
- Database setup provided through "ecommerce.sql"

---

## Technology Stack

Technology| Usage
Java| Core application development
MySQL| Database management
JDBC| Java-Database connectivity
HTML| Basic web interface
Bootstrap| UI styling
Font Awesome| Icons
IntelliJ IDEA| Development environment

---

## Concepts Used

This project demonstrates several important Computer Science concepts:

- Object-Oriented Programming (OOP)
- Data Structures
- Database Management Systems (DBMS)
- JDBC Connectivity
- SQL Queries
- CRUD Operations
- Authentication & Validation
- Modular Programming
- Exception Handling
- File Handling

---

## Data Structures

Custom data structures are implemented in the project to demonstrate their practical usage.

## Doubly Linked List

The project contains a custom "DoubleLinkedList" implementation for managing data using linked-list concepts.

## Stack

A custom "Stack" implementation is also included to demonstrate stack-based data management.

---

## Project Structure

E-Commerce/
│
├── src/
│   │
│   ├── ECommerce/
│   │   │
│   │   ├── DBMS/
│   │   │   ├── DBConnection.java
│   │   │   └── Table.java
│   │   │
│   │   ├── DS/
│   │   │   ├── DoubleLinkedList.java
│   │   │   └── Stack.java
│   │   │
│   │   ├── Model/
│   │   │   ├── BillingSystem.java
│   │   │   ├── CartItem.java
│   │   │   ├── User.java
│   │   │   ├── admin.java
│   │   │   ├── customer.java
│   │   │   ├── otp.java
│   │   │   ├── seller.java
│   │   │   └── validator.java
│   │   │
│   │   └── Main.java
│   │
│   ├── ecommerce.sql
│   └── index.html
│
├── .gitignore
└── README.md

## Database Setup

The project uses MySQL as its database.

Step 1: Install MySQL

Make sure MySQL Server is installed and running on your system.

Step 2: Create the Database

Open MySQL Workbench or MySQL Command Line and execute the SQL script provided in:

src/ecommerce.sql

This script contains the required database structure and data for the project.

Step 3: Configure Database Connection

Open:

src/ECommerce/DBMS/DBConnection.java

Update the database connection details according to your local MySQL configuration.


---

## How to Run

1. Clone the Repository

git clone <your-repository-url>

2. Open the Project

Open the project in IntelliJ IDEA or another Java IDE.

3. Configure MySQL

- Start MySQL Server
- Create/import the database using "ecommerce.sql"
- Update database credentials in "DBConnection.java"

4. Configure Dependencies

Make sure the required MySQL JDBC Driver is available in the project.

5. Run the Application

Run:

Main.java

The application will start from the main entry point.

---

## System Flow

User
  │
  ├── Customer
  │     ├── Register / Login
  │     ├── Browse Products
  │     ├── Add to Cart
  │     ├── Place Order
  │     └── Generate Bill
  │
  ├── Seller
  │     ├── Seller Operations
  │     ├── Manage Products
  │     └── Manage Orders
  │
  └── Admin
        └── Manage System Operations

                │
                ▼

        Java Application
                │
        ┌───────┴────────┐
        │                │
        ▼                ▼
  Data Structures       JDBC
                         │
                         ▼
                      MySQL
---

## Important Files

| File | Description |
|------|-------------|
| `Main.java` | Main entry point of the E-Commerce application |
| `DBConnection.java` | Establishes the connection between the Java application and MySQL database |
| `Table.java` | Handles database table-related operations |
| `BillingSystem.java` | Manages billing and bill generation functionality |
| `CartItem.java` | Represents products/items added to the shopping cart |
| `User.java` | Contains common user-related information and functionality |
| `customer.java` | Handles customer-related operations |
| `seller.java` | Handles seller-related operations |
| `admin.java` | Handles administrator-related operations |
| `otp.java` | Handles OTP-related functionality |
| `validator.java` | Provides input and user validation functionality |
| `DoubleLinkedList.java` | Custom implementation of a Doubly Linked List |
| `Stack.java` | Custom implementation of a Stack data structure |
| `ecommerce.sql` | Contains the MySQL database structure and required data |
| `index.html` | Provides the basic HTML-based web interface |

## Learning Objectives

This project helped in understanding how different Computer Science concepts can be combined to build a practical application.

Key learning outcomes:

- Implementing Java OOP concepts
- Working with custom data structures
- Connecting Java applications with MySQL
- Writing and executing SQL queries
- Managing users and roles
- Implementing cart and billing functionality
- Understanding database-driven application architecture
- Applying validation and authentication concepts

---

## Future Enhancements

The project can be further enhanced by adding:

- Modern responsive frontend
- Product search and filtering
- Product categories
- Product images
- Online payment integration
- Order tracking
- Product reviews and ratings
- Admin dashboard
- Seller dashboard
- REST API integration
- Improved authentication and authorization
- Better error handling and user experience

---


👩‍💻 Project Type

Academic / Educational Project

This project was developed to demonstrate practical implementation of:

- Java
- OOP
- Data Structures
- DBMS
- MySQL
- JDBC
- E-Commerce System Design

---
