-- Add pricing fields to products table for individual product discount model
ALTER TABLE products ADD COLUMN mrp DOUBLE;
ALTER TABLE products ADD COLUMN discount_percentage DOUBLE;

-- Initialize MRP with the current selling price for existing records
UPDATE products SET mrp = price WHERE mrp IS NULL;
