-- Migration: Add detailed address fields to users table
ALTER TABLE users ADD COLUMN floor VARCHAR(255);
ALTER TABLE users ADD COLUMN building_name VARCHAR(255);
ALTER TABLE users ADD COLUMN pincode VARCHAR(20);
ALTER TABLE users ADD COLUMN state VARCHAR(255);
