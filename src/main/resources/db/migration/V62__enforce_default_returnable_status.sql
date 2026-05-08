-- V62: Enforce Default Returnable Status and Clean Up Inconsistent Order States
-- 1. Ensure all existing products are returnable
UPDATE products SET is_returnable = TRUE WHERE is_returnable IS NULL OR is_returnable = FALSE;

-- 2. Ensure the column default is explicitly set to TRUE for future inserts
ALTER TABLE products MODIFY COLUMN is_returnable BOOLEAN DEFAULT TRUE;

-- 3. Cleanup: If an order is marked as RETURN_REQUESTED but no return record exists in the return_requests table,
-- revert it to DELIVERED so the user can re-initiate properly via the UI.
-- This fixes the "activeRet=NO" deadlock for historical manual test data.
UPDATE orders o 
SET o.status = 'DELIVERED' 
WHERE o.status = 'RETURN_REQUESTED' 
AND NOT EXISTS (SELECT 1 FROM return_requests rr WHERE rr.order_id = o.id);
