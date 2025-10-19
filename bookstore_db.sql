-- ==============================================================
-- BiblioFlow Database Setup Script
-- Clean + Executable (Preserves All Data & Table Definitions)
-- ==============================================================

-- 1️⃣ Create Database
CREATE DATABASE IF NOT EXISTS bookstore_db;
USE bookstore_db;

-- 2️⃣ Disable Foreign Key Checks (for Safe Drop)
SET FOREIGN_KEY_CHECKS = 0;

-- Drop Tables in Reverse Dependency Order
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS substore_books;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS orders;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================
-- 3️⃣ Create Users Table
-- ==============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================
-- 4️⃣ Create Books Table
-- ==============================================================
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    edition VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    rack_number VARCHAR(50),
    location VARCHAR(100),
    image_path VARCHAR(255),
    genre VARCHAR(100)
);

-- ==============================================================
-- 5️⃣ Create Substore Books Table
-- ==============================================================
CREATE TABLE substore_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    substore_name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- ==============================================================
-- 6️⃣ Create Bills Table
-- ==============================================================
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(15),
    total_amount DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    gst_amount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'Pending',
    order_status VARCHAR(20) DEFAULT 'Processing',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ==============================================================
-- 7️⃣ Create Bill Items Table
-- ==============================================================
CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    book_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- ==============================================================
-- 8️⃣ Insert Sample Books (20 Entries)
-- ==============================================================
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES 
('978-BOOK0000', 'The Temple Of The Ruby Of Fire', 'Elisabetta Dami', 'Unknown', NULL, 295, 7, 'R6 C3', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/The Temple Of The Ruby Of Fire.jpg', 'General'),
('978-BOOK0001', 'Attack Of The Bandit Cats', 'Elisabetta Dami', 'Unknown', NULL, 350, 8, 'R6 C3', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Attack Of The Bandit Cats.jpg', 'General'),
('978-BOOK0002', 'Paws Off, Cheddarface', 'Elisabetta Dami', 'Unknown', NULL, 295, 9, 'R6 C3', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Paws Off, Cheddarface.jpg', 'General'),
('978-BOOK0003', 'Digital Logic and Computer Design', 'M. Morris Mano', 'Unknown', '2.0', 520, 10, 'C10', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Digital Logic and Computer Design.jpg', 'General'),
('978-BOOK0004', 'Look Out Secret Seven', 'Enid Blyton', 'Unknown', NULL, 150, 4, 'R7 C4', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Look Out Secret Seven.jpg', 'General'),
('978-BOOK0005', 'Five Get Into Trouble', 'Enid Blyton', 'Unknown', NULL, 199, 10, 'R8 C5', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Five Get Into Trouble.jpg', 'General'),
('978-BOOK0006', 'The Naughtiest Girl', 'Enid Blyton', 'Unknown', NULL, 165, 2, 'R9 C1', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/The Naughtiest Girl.jpg', 'General'),
('978-BOOK0007', 'Gulliver''s Travels & Other Stories', 'Neela Subramaniam', 'Unknown', NULL, 45, 14, 'R12 C8', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Gullivers Travels & Other Stories.jpg', 'General'),
('978-BOOK0008', 'Digital Electronics and Logic Design', 'Marina Crompton, Kailas Sree Chandran', 'Unknown', '1.0', 300, 8, 'R13 C6', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Digital Electronics and Logic Design.jpg', 'General'),
('978-BOOK0009', 'ഒരച്ഛൻ മകൾക്കയച്ച കത്തുകൾ', 'Jawaharlal Nehru', 'Unknown', '19.0', 90, 4, 'A', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/ഒരച്ഛൻ മകൾക്കയച്ച കത്തുകൾ.jpg', 'General'),
('978-BOOK0010', 'Database System Concepts', 'Abraham Silberschatz', 'Unknown', '7.0', 600, 10, 'C3', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Database System Concepts.jpg', 'General'),
('978-BOOK0011', 'Engineering Entrepreneurship and IPR', 'Dr. Ajit Prabhu V, Dr. Vipin Gopan', 'Unknown', '1.0', 400, 9, 'R5 C5', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Engineering Entrepreneurship and IPR.jpg', 'General'),
('978-BOOK0012', '101 Essays for High & Higher Secondary Students', 'Preshant Gupta', 'Unknown', '4.0', 100, 5, 'R4 C7', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/101 Essays for High & Higher Secondary Students.jpg', 'General'),
('978-BOOK0013', 'Foundation of Computing: From Hardware to Web Design', 'Jyothy T J', 'Unknown', NULL, 300, 7, 'R6 C5', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Foundation of Computing From Hardware to Web Design.png', 'General'),
('978-BOOK0014', 'ഒരു സങ്കീർത്തനം പോലെ', 'Perumpadavom Sreedharan', 'Unknown', NULL, 60, 2, 'R1 C2', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/ഒരു സങ്കീർത്തനം പോലെ.jpg', 'General'),
('978-BOOK0015', 'Programming in ANSI C', 'E. Balagurusamy', 'Unknown', '8.0', 550, 18, 'C6', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Programming in ANSI C.jpg', 'General'),
('978-BOOK0016', 'ഖസാക്കിൻ്റ ഇതിഹാസം', 'O. V. Vijayan', 'Unknown', NULL, 160, 4, 'R1 C4', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/ഒരു സങ്കീർത്തനം പോലെ.jpg', 'General'),
('978-BOOK0017', 'War and Peace', 'Leo Tolstoy', 'Unknown', '6.0', 500, 4, 'R6', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/War and Peace.jpg', 'General'),
('978-BOOK0018', 'Microprocessor Architecture', 'Ramesh Gaonkar', 'Unknown', '4.0', 480, 6, 'C8', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/Microprocessor Architecture.jpg', 'General'),
('978-BOOK0019', 'A Good Friend and Other Moral Stories', 'Madavoor Sasi', 'Unknown', NULL, 60, 3, 'R2 C1', 'Library', 'D:/java project new/JAVA Project/BiblioFlow/imgs/A Good Friend and Other Moral Stories.png', 'General');


-- ==============================================================
-- 9️⃣ Create Orders Table (for OTP-based delivery)
-- ==============================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(15) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    order_status ENUM('Placed', 'Processing', 'Delivered', 'Completed', 'Cancelled') DEFAULT 'Placed',
    items_summary TEXT,
    otp VARCHAR(6),
    otp_generated_at TIMESTAMP NULL,
    otp_verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT,
    INDEX idx_order_number (order_number),
    INDEX idx_customer_phone (customer_phone),
    INDEX idx_status (order_status)
);

-- ==============================================================
-- 🔟 Sample Bills (5)
-- ==============================================================
INSERT INTO bills 
(bill_number, customer_name, customer_phone, total_amount, discount, gst_amount, final_amount, payment_method, payment_status, order_status, created_at) VALUES
('BILL1001', 'Alice Mathew', '9876543210', 1200.00, 0, 216.00, 1416.00, 'Cash', 'Paid', 'Delivered', '2025-10-01 10:20:00'),
('BILL1002', 'John Thomas', '9898989898', 850.00, 50.00, 144.00, 944.00, 'Card', 'Paid', 'Delivered', '2025-10-03 15:45:00'),
('BILL1003', 'Liya Shaji', '9123456789', 300.00, 0, 54.00, 354.00, 'UPI', 'Paid', 'Delivered', '2025-10-07 11:30:00'),
('BILL1004', 'Ben Joseph', '9012345678', 2000.00, 100.00, 342.00, 2242.00, 'Cash', 'Paid', 'Delivered', '2025-10-12 17:00:00'),
('BILL1005', 'Meera Mathew', '9001122334', 1500.00, 0, 270.00, 1770.00, 'UPI', 'Paid', 'Delivered', '2025-10-14 14:10:00');

-- ==============================================================
-- 1️⃣1️⃣ Bill Items
-- ==============================================================
INSERT INTO bill_items (bill_id, book_id, quantity, unit_price) VALUES
(1, 3, 1, 520.00),
(1, 10, 1, 600.00),
(1, 5, 1, 150.00),
(2, 16, 1, 550.00),
(2, 8, 1, 45.00),
(2, 7, 1, 165.00),
(3, 14, 1, 300.00),
(4, 17, 1, 500.00),
(4, 11, 1, 400.00),
(4, 19, 2, 480.00),
(5, 1, 1, 295.00),
(5, 2, 1, 350.00),
(5, 12, 1, 100.00),
(5, 15, 1, 60.00);

-- ==============================================================
-- 1️⃣2️⃣ Sample Orders (OTP System)
-- ==============================================================
INSERT INTO orders (order_number, customer_name, customer_phone, total_amount, order_status, items_summary, otp, otp_generated_at, otp_verified_at, user_id) VALUES
('ORD5001', 'Arun Joseph', '9887712345', 650.00, 'Delivered', 'Programming in ANSI C, Gulliver’s Travels', '456321', '2025-10-10 13:00:00', '2025-10-10 13:10:00', 3),
('ORD5002', 'Liya Shaji', '9123456789', 400.00, 'Completed', 'Foundation of Computing', '982145', '2025-10-11 11:30:00', '2025-10-11 11:45:00', 2),
('ORD5003', 'Meera Mathew', '9001122334', 1200.00, 'Processing', 'Digital Logic and Computer Design, Database System Concepts', NULL, NULL, NULL, 4),
('ORD5004', 'John Thomas', '9898989898', 720.00, 'Placed', 'Operating Systems Concepts', NULL, NULL, NULL, 1);
-- ==============================================================
-- 1️⃣3️⃣ Transactions
-- ==============================================================
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_payment_method (payment_method),
    INDEX idx_status (status)
);

INSERT INTO transactions (bill_id, payment_method, amount, status) VALUES
(1, 'Cash', 1500.00, 'SUCCESS'),
(1, 'Credit Card', 500.00, 'SUCCESS'),
(2, 'UPI', 3200.50, 'SUCCESS'),
(3, 'Cash', 875.25, 'SUCCESS'),
(4, 'Debit Card', 2100.75, 'SUCCESS');

-- ==============================================================
-- ✅ END OF SCRIPT
-- ==============================================================
