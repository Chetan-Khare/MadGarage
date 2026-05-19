-- Alter products table to add shipping attributes
ALTER TABLE products ADD COLUMN shipping_class VARCHAR(30) DEFAULT 'STANDARD' NOT NULL;
ALTER TABLE products ADD COLUMN weight_kg DOUBLE PRECISION DEFAULT 1.0;
ALTER TABLE products ADD COLUMN custom_shipping_cost DOUBLE PRECISION DEFAULT NULL;

-- Seed system settings for tiered shipping rates
INSERT INTO system_settings (config_key, config_value, description) 
VALUES ('SHIPPING_FEE_STANDARD', '150', 'Standard shipping cost for regular lightweight parts');

INSERT INTO system_settings (config_key, config_value, description) 
VALUES ('SHIPPING_FEE_FRAGILE', '1200', 'Special handling and custom wooden crating surcharge for fragile parts');

INSERT INTO system_settings (config_key, config_value, description) 
VALUES ('SHIPPING_FEE_FREIGHT_BASE', '2000', 'Base surface pallet shipping fee for heavy freight parts');

INSERT INTO system_settings (config_key, config_value, description) 
VALUES ('SHIPPING_FEE_FREIGHT_PER_KG', '15', 'Freight shipping rate per kg for heavy freight parts');
