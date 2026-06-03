-- Add mrp_at_purchase to order_items
ALTER TABLE order_items ADD COLUMN mrp_at_purchase DOUBLE;

-- Add total_savings to orders
ALTER TABLE orders ADD COLUMN total_savings DOUBLE;
