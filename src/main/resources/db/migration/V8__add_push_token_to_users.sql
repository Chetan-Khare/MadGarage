-- V8: Add expo_push_token column to users table with existence check
-- This prevents "Duplicate column name" errors during transition from Hibernate to Flyway.

DROP PROCEDURE IF EXISTS add_col_if_not_exists;
DELIMITER //
CREATE PROCEDURE add_col_if_not_exists()
BEGIN
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE() 
        AND TABLE_NAME = 'users' 
        AND COLUMN_NAME = 'expo_push_token'
    ) THEN
        ALTER TABLE users ADD COLUMN expo_push_token VARCHAR(255);
    END IF;
END //
DELIMITER ;

CALL add_col_if_not_exists();
DROP PROCEDURE add_col_if_not_exists;
