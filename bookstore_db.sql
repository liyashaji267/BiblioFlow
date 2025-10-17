-- ==============================
-- 1. Create Database
-- ==============================
CREATE DATABASE IF NOT EXISTS bookstore_db;
USE bookstore_db;

-- ==============================
-- 2. Drop Tables if they exist (for a clean start)
-- ==============================
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS substore_books;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS orders;

ALTER TABLE bills DROP FOREIGN KEY bills_ibfk_1;
ALTER TABLE bills MODIFY user_id INT NULL;
-- ==============================
-- 3. Create Users Table
-- ==============================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- 4. Create Books Table
-- ==============================
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

-- ==============================
-- 5. Create Substore Books Table
-- ==============================
CREATE TABLE substore_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    substore_name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- ==============================
-- 6. Create Bills Table
-- ==============================
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

-- ==============================
-- 7. Create Bill Items Table
-- ==============================
CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    book_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- ==============================
-- 8. Insert Sample Books (20)
-- ==============================
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES 
('978-BOOK0000', 'The Temple Of The Ruby Of Fire', 'Elisabetta Dami', 'Unknown', NULL, 295, 7, 'R6 C3', 'Library', 'images/book1.jpg', 'General'),
('978-BOOK0001', 'Attack Of The Bandit Cats', 'Elisabetta Dami', 'Unknown', NULL, 350, 8, 'R6 C3', 'Library', 'images/book2.jpg', 'General'),
('978-BOOK0002', 'Paws Off, Cheddarface', 'Elisabetta Dami', 'Unknown', NULL, 295, 9, 'R6 C3', 'Library', 'images/book3.jpg', 'General'),
('978-BOOK0003', 'Digital Logic and Computer Design', 'M. Morris Mano', 'Unknown', '2.0', 520, 10, 'C10', 'Library', 'images/book4.jpg', 'General'),
('978-BOOK0004', 'Look Out Secret Seven', 'Enid Blyton', 'Unknown', NULL, 150, 4, 'R7 C4', 'Library', 'images/book5.jpg', 'General'),
('978-BOOK0005', 'Five Get Into Trouble', 'Enid Blyton', 'Unknown', NULL, 199, 10, 'R8 C5', 'Library', 'images/book6.jpg', 'General'),
('978-BOOK0006', 'The Naughtiest Girl', 'Enid Blyton', 'Unknown', NULL, 165, 2, 'R9 C1', 'Library', 'images/book7.jpg', 'General'),
('978-BOOK0007', 'Gulliver''s Travels & Other Stories', 'Neela Subramaniam', 'Unknown', NULL, 45, 14, 'R12 C8', 'Library', 'images/book8.jpg', 'General'),
('978-BOOK0008', 'Digital Electronics and Logic Design', 'Marina Crompton, Kailas Sree Chandran', 'Unknown', '1.0', 300, 8, 'R13 C6', 'Library', 'images/book9.jpg', 'General'),
('978-BOOK0009', 'ഒരച്ഛൻ മകൾക്കയച്ച കത്തുകൾ', 'Jawaharlal Nehru', 'Unknown', '19.0', 90, 4, 'A', 'Library', 'images/book10.jpg', 'General'),
('978-BOOK0010', 'Database System Concepts', 'Abraham Silberschatz', 'Unknown', '7.0', 600, 10, 'C3', 'Library', 'images/book11.jpg', 'General'),
('978-BOOK0011', 'Engineering Entrepreneurship and IPR', 'Dr. Ajit Prabhu V, Dr. Vipin Gopan', 'Unknown', '1.0', 400, 9, 'R5 C5', 'Library', 'images/book12.jpg', 'General'),
('978-BOOK0012', '101 Essays for High & Higher Secondary Students', 'Preshant Gupta', 'Unknown', '4.0', 100, 5, 'R4 C7', 'Library', 'images/book13.jpg', 'General'),
('978-BOOK0013', 'Foundation of Computing: From Hardware to Web Design', 'Jyothy T J', 'Unknown', NULL, 300, 7, 'R6 C5', 'Library', 'images/book14.jpg', 'General'),
('978-BOOK0014', 'ഒരു സങ്കീർത്തനം പോലെ', 'Perumpadavom Sreedharan', 'Unknown', NULL, 60, 2, 'R1 C2', 'Library', 'images/book15.jpg', 'General'),
('978-BOOK0015', 'Programming in ANSI C', 'E. Balagurusamy', 'Unknown', '8.0', 550, 18, 'C6', 'Library', 'images/book16.jpg', 'General'),
('978-BOOK0016', 'ഖസാക്കിൻ്റ ഇതിഹാസം', 'O. V. Vijayan', 'Unknown', NULL, 160, 4, 'R1 C4', 'Library', 'images/book17.jpg', 'General'),
('978-BOOK0017', 'War and Peace', 'Leo Tolstoy', 'Unknown', '6.0', 500, 4, 'R6', 'Library', 'images/book18.jpg', 'General'),
('978-BOOK0018', 'Microprocessor Architecture', 'Ramesh Gaonkar', 'Unknown', '4.0', 480, 6, 'C8', 'Library', 'images/book19.jpg', 'General'),
('978-BOOK0019', 'A Good Friend and Other Moral Stories', 'Madavoor Sasi', 'Unknown', NULL, 60, 3, 'R2 C1', 'Library', 'images/book20.jpg', 'General');

-- ==============================
-- 9. Create Bills Table
-- ==============================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100),
    customer_phone VARCHAR(15) NOT NULL, -- Required for OTP delivery
    total_amount DECIMAL(10,2) NOT NULL,
    order_status ENUM('Placed', 'Processing', 'Delivered', 'Completed', 'Cancelled') DEFAULT 'Placed',
    items_summary TEXT, -- Stores book titles or order details
    otp VARCHAR(6), -- 6-digit OTP for delivery verification
    otp_generated_at TIMESTAMP NULL, -- When OTP was generated
    otp_verified_at TIMESTAMP NULL, -- When OTP was verified
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT,
    INDEX idx_order_number (order_number),
    INDEX idx_customer_phone (customer_phone),
    INDEX idx_status (order_status)
);

-- ==============================
-- 10. Sample Bills (5 Bills)
-- ==============================
INSERT INTO bills 
(bill_number, customer_name, customer_phone, total_amount, discount, gst_amount, final_amount, payment_method, payment_status, order_status, created_at) VALUES
('BILL1001', 'Alice Mathew', '9876543210', 1200.00, 0, 216.00, 1416.00, 'Cash', 'Paid', 'Delivered', '2025-10-01 10:20:00'),
('BILL1002', 'John Thomas', '9898989898', 850.00, 50.00, 144.00, 944.00, 'Card', 'Paid', 'Delivered', '2025-10-03 15:45:00'),
('BILL1003', 'Liya Shaji', '9123456789', 300.00, 0, 54.00, 354.00, 'UPI', 'Paid', 'Delivered', '2025-10-07 11:30:00'),
('BILL1004', 'Ben Joseph', '9012345678', 2000.00, 100.00, 342.00, 2242.00, 'Cash', 'Paid', 'Delivered', '2025-10-12 17:00:00'),
('BILL1005', 'Meera Mathew', '9001122334', 1500.00, 0, 270.00, 1770.00, 'UPI', 'Paid', 'Delivered', '2025-10-14 14:10:00');


-- ==============================
-- 11. Sample Bill Items (Books Sold per Bill)
-- ==============================
INSERT INTO bill_items (bill_id, book_id, quantity, unit_price) VALUES
-- Bill 1 (Alice)
(1, 3, 1, 520.00),   -- Digital Logic
(1, 10, 1, 600.00),  -- Database System Concepts
(1, 5, 1, 150.00),   -- Look Out Secret Seven

-- Bill 2 (John)
(2, 16, 1, 550.00),  -- Programming in ANSI C
(2, 8, 1, 45.00),    -- Gulliver’s Travels
(2, 7, 1, 165.00),   -- The Naughtiest Girl

-- Bill 3 (Liya)
(3, 14, 1, 300.00),  -- Foundation of Computing

-- Bill 4 (Ben)
(4, 17, 1, 500.00),  -- War and Peace
(4, 11, 1, 400.00),  -- Engineering Entrepreneurship
(4, 19, 2, 480.00),  -- Microprocessor Architecture (x2)

-- Bill 5 (Meera)
(5, 1, 1, 295.00),   -- The Temple Of The Ruby Of Fire
(5, 2, 1, 350.00),   -- Attack Of The Bandit Cats
(5, 12, 1, 100.00),  -- 101 Essays
(5, 15, 1, 60.00);   -- ഒരു സങ്കീർത്തനം പോലെ

-- ==============================
-- 12. Sample Orders (for OTP system)
-- ==============================
INSERT INTO orders (order_number, customer_name, customer_phone, total_amount, order_status, items_summary, otp, otp_generated_at, otp_verified_at, user_id) VALUES
('ORD5001', 'Arun Joseph', '9887712345', 650.00, 'Delivered', 'Programming in ANSI C, Gulliver’s Travels', '456321', '2025-10-10 13:00:00', '2025-10-10 13:10:00', 3),
('ORD5002', 'Liya Shaji', '9123456789', 400.00, 'Completed', 'Foundation of Computing', '982145', '2025-10-11 11:30:00', '2025-10-11 11:45:00', 2),
('ORD5003', 'Meera Mathew', '9001122334', 1200.00, 'Processing', 'Digital Logic and Computer Design, Database System Concepts', NULL, NULL, NULL, 4),
('ORD5004', 'John Thomas', '9898989898', 720.00, 'Placed', 'Operating Systems Concepts', NULL, NULL, NULL, 1);