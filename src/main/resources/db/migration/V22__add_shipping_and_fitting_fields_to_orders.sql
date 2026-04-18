-- V22: Add Shipping and Garage Fitting fields to the orders table
-- This fixes the SQLGrammarException (Unknown column 'delivery_type')

ALTER TABLE orders 
ADD COLUMN delivery_type VARCHAR(50) DEFAULT 'HOME_DELIVERY',
ADD COLUMN fitting_garage_id BIGINT,
ADD COLUMN fitting_status VARCHAR(50) DEFAULT 'NONE';

-- Update existing records to have a safe baseline
UPDATE orders SET delivery_type = 'HOME_DELIVERY' WHERE delivery_type IS NULL;
UPDATE orders SET fitting_status = 'NONE' WHERE fitting_status IS NULL;
