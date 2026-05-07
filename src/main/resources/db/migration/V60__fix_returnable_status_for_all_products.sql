-- Fix is_returnable status for all existing products to ensure they are returnable by default
UPDATE products SET is_returnable = TRUE WHERE is_returnable IS NULL OR is_returnable = FALSE;
