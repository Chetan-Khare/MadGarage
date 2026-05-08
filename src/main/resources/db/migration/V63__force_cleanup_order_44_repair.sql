-- V63: Force-Activate Return for Order #44 for Testing (Updated)
-- 1. If a return exists but is owned by the wrong user, fix it
UPDATE return_requests rr
JOIN orders o ON rr.order_id = o.id
SET rr.user_id = o.user_id
WHERE o.id = 44;

-- 2. If NO return exists, create a fresh one automatically so activeRet becomes YES
INSERT INTO return_requests (order_id, user_id, reason, request_type, description, status, requested_at)
SELECT id, user_id, 'WRONG_FITMENT', 'REPLACEMENT', 'Automatic protocol activation for testing', 'PENDING', NOW()
FROM orders
WHERE id = 44
AND NOT EXISTS (SELECT 1 FROM return_requests WHERE order_id = 44);

-- 3. Ensure order status reflects the active return
UPDATE orders SET status = 'RETURN_REQUESTED' WHERE id = 44;
