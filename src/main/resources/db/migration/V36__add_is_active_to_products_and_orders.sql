-- V36: Add is_active column to products and orders for soft-deletion support
ALTER TABLE products ADD COLUMN is_active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE orders ADD COLUMN is_active BOOLEAN DEFAULT TRUE NOT NULL;
