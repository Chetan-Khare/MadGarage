-- V24: Create System Settings Table for dynamic application configuration
CREATE TABLE system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- Seed initial pricing data
INSERT INTO system_settings (config_key, config_value, description) VALUES 
('SHIPPING_FEE', '250', 'Standard shipping fee applied to orders'),
('FREE_SHIPPING_THRESHOLD', '400', 'Minimum order amount for free shipping');
