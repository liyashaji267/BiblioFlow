CREATE DATABASE IF NOT EXISTS BarcodeDetails_db;
USE BarcodeDetails_db;

CREATE TABLE books_details (
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

INSERT INTO books_details (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) VALUES 
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

SELECT COUNT(*) FROM books_details;
SELECT isbn, title, price FROM books_details LIMIT 10;
