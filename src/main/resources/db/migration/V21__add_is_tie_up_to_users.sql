-- V21: Add is_tie_up field to users table
-- This field handles the business tie-up status for partners.

ALTER TABLE users 
ADD COLUMN is_tie_up BOOLEAN DEFAULT FALSE AFTER is_active;
