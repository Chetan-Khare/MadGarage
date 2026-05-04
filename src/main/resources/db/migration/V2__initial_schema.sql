-- V2: Initial Schema for Core Tables (Users, Products, Orders, OrderItems)
-- This file restores the baseline schema that was previously managed by Hibernate ddl-auto.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    profile_image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    brand VARCHAR(100),
    part_name VARCHAR(255),
    category VARCHAR(100),
    price DOUBLE,
    description TEXT,
    image_url VARCHAR(255),
    color VARCHAR(50),
    stock_quantity INT,
    installation_guide_url VARCHAR(255),
    fitment_category VARCHAR(100),
    part_condition VARCHAR(100),
    seller_id BIGINT,
    CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subtotal DOUBLE,
    tax_amount DOUBLE,
    shipping_fee DOUBLE,
    platform_fee DOUBLE,
    grand_total DOUBLE,
    status VARCHAR(50),
    order_date DATETIME,
    shipping_address VARCHAR(512),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT,
    price_at_purchase DOUBLE,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS product_fitment (
    product_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, vehicle_id),
    CONSTRAINT fk_pf_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Seed data 
INSERT INTO users (first_name, last_name, email, password, role, is_active, created_at, updated_at) VALUES 
('Admin', 'User', 'admin@madgarage.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'ROLE_ADMIN', true, NOW(), NOW()),
('Premium', 'Seller', 'seller@madgarage.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'ROLE_SELLER', true, NOW(), NOW());
