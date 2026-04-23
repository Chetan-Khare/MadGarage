-- V28: Add Tiered Garage Discount settings for range-based pricing
-- Low tier: standard parts (below MID_THRESHOLD) get the highest discount
-- Mid tier: medium parts (between MID and HIGH thresholds) get moderate discount
-- High tier: premium parts (above HIGH_THRESHOLD) get lowest discount to protect margins

INSERT INTO system_settings (config_key, config_value, description) VALUES
('GARAGE_DISCOUNT_LOW_PERCENT', '5', 'Discount % for products below the mid threshold (default tier)'),
('GARAGE_DISCOUNT_MID_THRESHOLD', '10000', 'Price threshold (₹) above which mid-tier discount applies'),
('GARAGE_DISCOUNT_MID_PERCENT', '3', 'Discount % for products between mid and high thresholds'),
('GARAGE_DISCOUNT_HIGH_THRESHOLD', '50000', 'Price threshold (₹) above which high-tier discount applies'),
('GARAGE_DISCOUNT_HIGH_PERCENT', '1', 'Discount % for premium products above the high threshold');
