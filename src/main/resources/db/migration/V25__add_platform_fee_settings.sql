ALTER TABLE orders ADD COLUMN platform_fee DOUBLE DEFAULT 0.0;
INSERT INTO system_settings (config_key, config_value, description) VALUES ('PLATFORM_FEE', '7', 'Standard platform fee for all orders');
