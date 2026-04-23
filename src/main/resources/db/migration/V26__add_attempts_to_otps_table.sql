-- V26: Add attempts column to otps table for security lockout logic
ALTER TABLE otps ADD COLUMN attempts INT NOT NULL DEFAULT 0;
