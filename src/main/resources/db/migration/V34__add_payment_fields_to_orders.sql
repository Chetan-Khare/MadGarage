-- V34: Add missing payment fields to orders table (Portability Optimized)
-- Replaced hardcoded schema name with DATABASE() to support different environment names.
DROP PROCEDURE IF EXISTS AddPaymentColumns;
DELIMITER //
CREATE PROCEDURE AddPaymentColumns()
BEGIN
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'payment_id') THEN
        ALTER TABLE orders ADD COLUMN payment_id VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'payment_signature') THEN
        ALTER TABLE orders ADD COLUMN payment_signature VARCHAR(255);
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'payment_verified') THEN
        ALTER TABLE orders ADD COLUMN payment_verified BOOLEAN DEFAULT FALSE;
    END IF;
END //
DELIMITER ;
CALL AddPaymentColumns();
DROP PROCEDURE AddPaymentColumns;
