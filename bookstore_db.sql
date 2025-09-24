DROP TABLE IF EXISTS books;
CREATE DATABASE IF NOT EXISTS bookstore_db;
USE bookstore_db;

-- Users table for login/signup
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Books table for main store inventory
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

-- Substore books table
CREATE TABLE substore_books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    substore_name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Bills table
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

-- Bill items table
CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    book_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);


INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0000', ' The Temple Of The Ruby Of Fire', 'Elisabetta Dami', 'Unknown', NULL, 295, 7, 'R6 C3', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0001', 'Attack Of The Bandit Cats', 'Elisabetta Dami', 'Unknown', NULL, 350, 8, 'R6 C3', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0002', 'Paws Off , Cheddarface  ', 'Elisabetta Dami', 'Unknown', NULL, 295, 9, 'R6 C3', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0003', 'Digital Logic and Computer Design', 'M. Morris Mano', 'Unknown', '2.0', 520, 10, 'C10', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0004', ' Look Out Secret Seven', 'Enid Blyton', 'Unknown', NULL, 150, 4, 'R7 C4', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0005', 'Five Get Into Trouble', 'Enid Blyton', 'Unknown', NULL, 199, 10, 'R8 C5', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0006', 'The Naughtiest Girl', 'Enid Blyton', 'Unknown', NULL, 165, 2, 'R9 C1', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0007', 'Gulliver''s Travels Other Stories', 'Neela Subramaniam', 'Unknown', NULL, 45, 14, 'R12 C8', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0008', ' Digitale Eletronices And Logic design', 'Marina Crompton And Kailas Sree Chandran', 'Unknown', '1.0', 300, 8, 'R13 C6', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0009', 'ഒരച്ഛൻ മകൾക്കയച്ച കത്തുകൾ', 'Jawaharlal Nehru', 'Unknown', '19.0', 90, 4, 'a', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0010', 'Database System Concepts', 'Abraham Silberschatz', 'Unknown', '7.0', 600, 10, 'C3', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0011', 'Engineering Enterpereneurship And Intellectual Property Rights', 'DR Ajit Prabhu V AndDR Vipin Gopan', 'Unknown', '1.0', 400, 9, 'R5 C5', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0012', '101 Essays For High And Higher Secondary Studends', 'Preshant Gupta', 'Unknown', '4.0', 100, 5, 'R4 C7', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0013', 'Foudation Of Computing ; From Hardware to Wed Design', 'Jyothy T J', 'Unknown', NULL, 300, 7, 'R6 C5', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0014', 'ഒരു സങ്കീർത്തനം പോലെ', 'Perumpadavom Sreedharan', 'Unknown', NULL, 60, 2, 'R1 C2', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0015', 'Programming in ANSI C', 'E. Balagurusamy', 'Unknown', '8.0', 550, 18, 'C6', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0016', 'ഖസാക്കിൻ്റ ഇതിഹാസം', 'O. V. Vijayan', 'Unknown', NULL, 160, 4, 'R1 C4', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0017', 'War and Peace', 'Leo Tolstoy', 'Unknown', '6.0', 500, 4, 'R6', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0018', 'Microprocessor Architecture', 'Ramesh Gaonkar', 'Unknown', '4.0', 480, 6, 'C8', 'Library', NULL, 'General');
INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES ('978-BOOK0019', 'A Good Friend And Other Moral Stories', 'Madavoor Sasi', 'Unknown', NULL, 60, 3, 'R2 C1', 'Library', NULL, 'General');


