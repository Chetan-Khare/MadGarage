-- Add delivered_at column to track actual delivery time for return window calculations
ALTER TABLE orders ADD COLUMN delivered_at DATETIME;

-- Initialize delivered_at for existing DELIVERED orders using current time as a grace period
UPDATE orders SET delivered_at = CURRENT_TIMESTAMP WHERE status = 'DELIVERED' AND delivered_at IS NULL;
