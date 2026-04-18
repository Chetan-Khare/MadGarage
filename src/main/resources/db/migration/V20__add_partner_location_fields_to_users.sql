-- V20: Add location and physical address fields to users table for partner location management
-- These fields support the "Workshop Logistics" and "Business Location" features.

ALTER TABLE users 
ADD COLUMN city VARCHAR(100) AFTER expo_push_token,
ADD COLUMN address VARCHAR(512) AFTER city,
ADD COLUMN latitude DOUBLE AFTER address,
ADD COLUMN longitude DOUBLE AFTER latitude;
