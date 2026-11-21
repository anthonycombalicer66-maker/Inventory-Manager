CREATE DATABASE IF NOT EXISTS inventory_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE inventory_db;

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

INSERT INTO products (product_name, category, quantity, price) VALUES
('Wireless Mouse', 'Electronics', 25, 599.00),
('Office Chair', 'Office', 10, 3499.50),
('Desk Lamp', 'Home', 40, 450.00);
