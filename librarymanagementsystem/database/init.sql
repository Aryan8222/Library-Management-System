

USE library_management;

-- Create Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    membership_type ENUM('REGULAR', 'PREMIUM', 'STUDENT') DEFAULT 'REGULAR',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Books table
CREATE TABLE IF NOT EXISTS books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    genre VARCHAR(100),
    publication_year INT,
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Admins table
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('SUPER_ADMIN', 'ADMIN', 'LIBRARIAN') DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Borrow Records table
CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME NOT NULL,
    return_date DATETIME NULL,
    status ENUM('BORROWED', 'RETURNED', 'OVERDUE') DEFAULT 'BORROWED',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Insert sample users
INSERT IGNORE INTO users (username, email, first_name, last_name, phone, membership_type) VALUES
('john_doe', 'john@example.com', 'John', 'Doe', '1234567890', 'REGULAR'),
('jane_smith', 'jane@example.com', 'Jane', 'Smith', '0987654321', 'PREMIUM'),
('alice_student', 'alice@student.edu', 'Alice', 'Johnson', '5555555555', 'STUDENT'),
('bob_wilson', 'bob@example.com', 'Bob', 'Wilson', '4444444444', 'REGULAR'),
('charlie_brown', 'charlie@example.com', 'Charlie', 'Brown', '3333333333', 'PREMIUM');

-- Insert sample books
INSERT IGNORE INTO books (title, author, isbn, genre, publication_year, total_copies, available_copies) VALUES
('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Fiction', 1960, 3, 2),
('1984', 'George Orwell', '9780451524935', 'Dystopian Fiction', 1949, 5, 4),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 'Romance', 1813, 2, 2),
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Classic Literature', 1925, 4, 3),
('Dune', 'Frank Herbert', '9780441172719', 'Science Fiction', 1965, 3, 2),
('The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', '9780345391803', 'Science Fiction', 1979, 2, 2),
('Sapiens', 'Yuval Noah Harari', '9780062316097', 'History', 2014, 3, 1),
('Clean Code', 'Robert C. Martin', '9780132350884', 'Technology', 2008, 4, 3),
('The Art of War', 'Sun Tzu', '9781599869773', 'Philosophy', -500, 2, 2),
('Design Patterns', 'Gang of Four', '9780201633610', 'Technology', 1994, 2, 1);

-- Insert some sample borrow records
INSERT IGNORE INTO borrow_records (user_id, book_id, borrow_date, due_date, status) VALUES
(1, 1, '2024-08-01 10:00:00', '2024-08-15 10:00:00', 'RETURNED'),
(2, 2, '2024-08-10 14:30:00', '2025-08-24 14:30:00', 'BORROWED'),
(3, 3, '2024-08-05 09:15:00', '2025-08-19 09:15:00', 'BORROWED');

-- Grant privileges
GRANT ALL PRIVILEGES ON library_management.* TO 'library_user'@'%';
FLUSH PRIVILEGES;

-- Show created tables
SHOW TABLES;

-- Display sample data counts
SELECT 'Users' as TableName, COUNT(*) as RecordCount FROM users
UNION ALL
SELECT 'Books' as TableName, COUNT(*) as RecordCount FROM books
UNION ALL
SELECT 'Borrow Records' as TableName, COUNT(*) as RecordCount FROM borrow_records;