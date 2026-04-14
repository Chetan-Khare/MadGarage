-- Migration V19: Add manual rating value and override toggle to products
ALTER TABLE products ADD COLUMN manual_rating DOUBLE;
ALTER TABLE products ADD COLUMN manual_rating_override BOOLEAN DEFAULT FALSE;
