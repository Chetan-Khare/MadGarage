-- V29: Add wholesale flag to products table
-- When wholesale = TRUE (default), garage discount tiers apply
-- When wholesale = FALSE, the product sells at full retail price for everyone

ALTER TABLE products ADD COLUMN wholesale BOOLEAN NOT NULL DEFAULT TRUE;
