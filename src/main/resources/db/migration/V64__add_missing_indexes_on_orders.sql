-- V64__add_missing_indexes_on_orders.sql
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
