-- Migration V17: Add audit and response fields to products table
ALTER TABLE products ADD COLUMN flag_reason VARCHAR(1000);
ALTER TABLE products ADD COLUMN seller_response VARCHAR(1000);
