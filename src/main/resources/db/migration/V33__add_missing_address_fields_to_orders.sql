-- V33: Add missing address fields to orders table
-- Using V33 to bypass the failed V32 entry.
-- We use a procedure to safely add columns if they don't exist.
DROP PROCEDURE IF EXISTS AddColumnIfMissing;
DELIMITER //
CREATE PROCEDURE AddColumnIfMissing()
BEGIN
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'madgarage' AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'city') THEN
        ALTER TABLE orders ADD COLUMN city VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'madgarage' AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'state') THEN
        ALTER TABLE orders ADD COLUMN state VARCHAR(100);
    END IF;
    IF NOT EXISTS (SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'madgarage' AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'pincode') THEN
        ALTER TABLE orders ADD COLUMN pincode VARCHAR(10);
    END IF;
END //
DELIMITER ;
CALL AddColumnIfMissing();
DROP PROCEDURE AddColumnIfMissing;
