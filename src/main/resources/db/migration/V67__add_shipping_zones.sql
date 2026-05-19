-- V67: Add shipping_zones table and seed India-wide geographic freight zones.
-- Also seed multipliers for HEAVY_FREIGHT tier distance-based transport pricing.

CREATE TABLE shipping_zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state_name VARCHAR(100) NOT NULL UNIQUE,
    zone_id INT NOT NULL
);

-- Seed all 36 states and Union Territories into 6 zones
INSERT INTO shipping_zones (state_name, zone_id) VALUES
  ('Delhi', 1), ('Haryana', 1), ('Punjab', 1), ('Rajasthan', 1),
  ('Uttar Pradesh', 1), ('Uttarakhand', 1), ('Himachal Pradesh', 1),
  ('Jammu and Kashmir', 1), ('Jammu & Kashmir', 1), ('Ladakh', 1),
  ('Maharashtra', 2), ('Gujarat', 2), ('Goa', 2),
  ('Dadra and Nagar Haveli', 2), ('Daman and Diu', 2),
  ('Karnataka', 3), ('Tamil Nadu', 3), ('Kerala', 3),
  ('Andhra Pradesh', 3), ('Telangana', 3), ('Puducherry', 3), ('Lakshadweep', 3),
  ('West Bengal', 4), ('Bihar', 4), ('Jharkhand', 4), ('Odisha', 4),
  ('Andaman and Nicobar Islands', 4), ('Andaman & Nicobar', 4),
  ('Madhya Pradesh', 5), ('Chhattisgarh', 5),
  ('Assam', 6), ('Meghalaya', 6), ('Manipur', 6), ('Nagaland', 6),
  ('Mizoram', 6), ('Tripura', 6), ('Arunachal Pradesh', 6), ('Sikkim', 6);

-- Seed HEAVY_FREIGHT zone multipliers
INSERT INTO system_settings (config_key, config_value, description) VALUES
  ('FREIGHT_ZONE_MULTIPLIER_0', '1.0',  'HEAVY_FREIGHT multiplier: same zone (e.g. Delhi -> Haryana)'),
  ('FREIGHT_ZONE_MULTIPLIER_1', '1.25', 'HEAVY_FREIGHT multiplier: 1 zone apart (e.g. Mumbai -> Bangalore)'),
  ('FREIGHT_ZONE_MULTIPLIER_2', '1.5',  'HEAVY_FREIGHT multiplier: 2 zones apart (e.g. Mumbai -> Kolkata)'),
  ('FREIGHT_ZONE_MULTIPLIER_3', '1.75', 'HEAVY_FREIGHT multiplier: 3 zones apart (e.g. Delhi -> Chennai)'),
  ('FREIGHT_ZONE_MULTIPLIER_4', '2.0',  'HEAVY_FREIGHT multiplier: 4+ zones apart (e.g. Delhi -> Goa)'),
  ('FREIGHT_ZONE_MULTIPLIER_NE', '2.25','HEAVY_FREIGHT multiplier: any delivery to Northeast (always overrides)');
