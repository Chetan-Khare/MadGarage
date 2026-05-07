-- Add is_returnable column to products table
ALTER TABLE products ADD COLUMN is_returnable BOOLEAN DEFAULT TRUE;
