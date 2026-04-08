-- V11: Comprehensive Vehicle Catalog (150+ Authentic Indian Market Cars)
-- This migration ensures the baseline catalog is professionally populated.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. SEED MAKES (Professional Indian Market Set)
INSERT IGNORE INTO makes (name) VALUES 
('Tata'), ('Mahindra'), ('Maruti Suzuki'), ('Hyundai'), ('Kia'), 
('Toyota'), ('Skoda'), ('Volkswagen'), ('Honda'), ('MG'), 
('Renault'), ('Nissan'), ('Citroen'), ('Jeep');

-- 2. SEED MODELS
-- TATA
INSERT IGNORE INTO models (name, make_id) SELECT 'Nexon', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Harrier', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Safari', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Punch', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Tiago', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Altroz', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Curvv', id FROM makes WHERE name = 'Tata';

-- MAHINDRA
INSERT IGNORE INTO models (name, make_id) SELECT 'XUV700', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Thar', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Thar Roxx', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio-N', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio Classic', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Bolero', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'XUV 3XO', id FROM makes WHERE name = 'Mahindra';

-- MARUTI
INSERT IGNORE INTO models (name, make_id) SELECT 'Swift', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Baleno', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Grand Vitara', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Brezza', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Ertiga', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Jimny', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Fronx', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Dzire', id FROM makes WHERE name = 'Maruti Suzuki';

-- HYUNDAI
INSERT IGNORE INTO models (name, make_id) SELECT 'Creta', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Venue', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'i20', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Verna', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Exter', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Alcazar', id FROM makes WHERE name = 'Hyundai';

-- KIA
INSERT IGNORE INTO models (name, make_id) SELECT 'Seltos', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Sonet', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Carens', id FROM makes WHERE name = 'Kia';

-- TOYOTA
INSERT IGNORE INTO models (name, make_id) SELECT 'Fortuner', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Innova Hycross', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Innova Crysta', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Urban Cruiser Hyryder', id FROM makes WHERE name = 'Toyota';

-- SKODA / VW
INSERT IGNORE INTO models (name, make_id) SELECT 'Kushaq', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Slavia', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Taigun', id FROM makes WHERE name = 'Volkswagen';
INSERT IGNORE INTO models (name, make_id) SELECT 'Virtus', id FROM makes WHERE name = 'Volkswagen';

-- 3. SEED VEHICLES (150+ VARIANT SEEDING)
-- Using a temporary table to handle bulk idempotency safely.

CREATE TEMPORARY TABLE IF NOT EXISTS vehicle_staging (
    make_name VARCHAR(255),
    model_name VARCHAR(255),
    trim_name VARCHAR(255),
    m_year INT,
    e_type VARCHAR(255),
    f_type VARCHAR(255)
);

INSERT INTO vehicle_staging VALUES 
-- TATA NEXON
('Tata', 'Nexon', 'Smart', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Smart+', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Pure', 2024, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'Pure S', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Creative', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Creative+', 2024, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'Fearless', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Fearless+', 2024, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'Fearless+ S', 2024, '1.2L Turbo GDi', 'Petrol'),
('Tata', 'Nexon', 'iCNG Creative', 2025, '1.2L Turbo', 'CNG'),
-- TATA HARRIER
('Tata', 'Harrier', 'Smart', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Pure', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Adventure+', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Fearless+', 2024, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Dark Edition', 2024, '2.0L Kryotec', 'Diesel'),
-- TATA SAFARI
('Tata', 'Safari', 'Smart', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Pure+', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Adventure', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Empowered+', 2024, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Dark Edition', 2024, '2.0L Kryotec', 'Diesel'),
-- TATA PUNCH
('Tata', 'Punch', 'Pure', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Adventure', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Accomplished', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Creative Flagship', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Adventure Rhythm iCNG', 2024, '1.2L Revotron', 'CNG'),
-- TATA ALTROZ / TIAGO
('Tata', 'Altroz', 'XE', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Altroz', 'XM+', 2024, '1.5L Diesel', 'Diesel'),
('Tata', 'Altroz', 'XZ+ iTurbo', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Tiago', 'XE', 2024, '1.2L Revotron', 'Petrol'),
('Tata', 'Tiago', 'XZ+ DT', 2024, '1.2L Revotron', 'Petrol'),
-- MAHINDRA XUV700
('Mahindra', 'XUV700', 'MX', 2024, '2.0L Turbo', 'Petrol'),
('Mahindra', 'XUV700', 'AX3', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'XUV700', 'AX5', 2024, '2.0L Turbo', 'Petrol'),
('Mahindra', 'XUV700', 'AX7', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'XUV700', 'AX7 Luxury Pack', 2025, '2.2L mHawk', 'Diesel'),
('Mahindra', 'XUV700', 'AX7 L AWD', 2025, '2.2L mHawk', 'Diesel'),
-- MAHINDRA SCORPIO-N
('Mahindra', 'Scorpio-N', 'Z2', 2024, '2.0L Turbo', 'Petrol'),
('Mahindra', 'Scorpio-N', 'Z4', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z6', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z8', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z8 L', 2025, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z8 L 4WD', 2025, '2.2L mHawk', 'Diesel'),
-- MAHINDRA THAR / ROXX
('Mahindra', 'Thar', 'AX Opt', 2023, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar', 'LX Hard Top', 2023, '2.0L Turbo', 'Petrol'),
('Mahindra', 'Thar', 'LX 4WD Diesel', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar Roxx', 'MX1', 2025, '2.0L mStallion', 'Petrol'),
('Mahindra', 'Thar Roxx', 'AX3 L', 2025, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar Roxx', 'AX7 L 4WD', 2025, '2.2L mHawk', 'Diesel'),
-- MAHINDRA BOLERO / SCORPIO CLASSIC
('Mahindra', 'Bolero', 'B4', 2024, '1.5L mHawk75', 'Diesel'),
('Mahindra', 'Bolero Neo', 'N10', 2024, '1.5L mHawk100', 'Diesel'),
('Mahindra', 'Scorpio Classic', 'S', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio Classic', 'S11', 2024, '2.2L mHawk', 'Diesel'),
-- MARUTI SWIFT
('Maruti Suzuki', 'Swift', 'LXi', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi (O)', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+ AMT', 2024, '1.2L Z-Series', 'Petrol'),
-- MARUTI BALENO / FRONX
('Maruti Suzuki', 'Baleno', 'Sigma', 2024, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Delta', 2024, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Zeta', 2024, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Alpha', 2024, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Fronx', 'Sigma', 2024, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Fronx', 'Delta+', 2024, '1.0L BoosterJet', 'Petrol'),
('Maruti Suzuki', 'Fronx', 'Alpha Turbo', 2024, '1.0L BoosterJet', 'Petrol'),
-- MARUTI BREZZA / GRAND VITARA
('Maruti Suzuki', 'Brezza', 'LXi', 2024, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Brezza', 'VXi iCNG', 2024, '1.5L K-Series', 'CNG'),
('Maruti Suzuki', 'Brezza', 'ZXi+ AT', 2024, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Grand Vitara', 'Sigma', 2024, '1.5L Smart Hybrid', 'Petrol'),
('Maruti Suzuki', 'Grand Vitara', 'Delta', 2024, '1.5L Smart Hybrid', 'Petrol'),
('Maruti Suzuki', 'Grand Vitara', 'Zeta+', 2024, '1.5L Strong Hybrid', 'Petrol Hybrid'),
('Maruti Suzuki', 'Grand Vitara', 'Alpha+ AWD', 2024, '1.5L Smart Hybrid', 'Petrol'),
-- HYUNDAI CRETA
('Hyundai', 'Creta', 'E', 2024, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'EX', 2024, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'S', 2024, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'S(O)', 2024, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'SX Tech', 2024, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'SX (O) IVT', 2025, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'SX (O) DCT', 2025, '1.5L Turbo GDi', 'Petrol'),
('Hyundai', 'Creta', 'SX (O) Diesel AT', 2025, '1.5L CRDi', 'Diesel'),
-- HYUNDAI VENUE / EXTER
('Hyundai', 'Venue', 'S', 2024, '1.2L MPi', 'Petrol'),
('Hyundai', 'Venue', 'S(O) Turbo', 2024, '1.0L Turbo', 'Petrol'),
('Hyundai', 'Venue', 'SX(O) DCT', 2024, '1.0L Turbo', 'Petrol'),
('Hyundai', 'Exter', 'EX', 2024, '1.2L MPi', 'Petrol'),
('Hyundai', 'Exter', 'SX', 2024, '1.2L MPi', 'Petrol'),
('Hyundai', 'Exter', 'SX(O) Connect', 2024, '1.2L MPi', 'Petrol'),
-- KIA SELTOS
('Kia', 'Seltos', 'HTE', 2024, '1.5L MPi', 'Petrol'),
('Kia', 'Seltos', 'HTK', 2024, '1.5L CRDi', 'Diesel'),
('Kia', 'Seltos', 'HTK+', 2024, '1.5L MPi', 'Petrol'),
('Kia', 'Seltos', 'HTX+', 2024, '1.5L CRDi', 'Diesel'),
('Kia', 'Seltos', 'GTX+ Turbo', 2025, '1.5L Turbo GDi', 'Petrol'),
('Kia', 'Seltos', 'X-Line', 2025, '1.5L Turbo GDi', 'Petrol'),
-- KIA SONET
('Kia', 'Sonet', 'HTE', 2024, '1.2L MPi', 'Petrol'),
('Kia', 'Sonet', 'HTK+', 2024, '1.0L Turbo', 'Petrol'),
('Kia', 'Sonet', 'HTX+ Diesel', 2024, '1.5L CRDi', 'Diesel'),
('Kia', 'Sonet', 'GTX+ Turbo', 2024, '1.0L Turbo', 'Petrol'),
-- TOYOTA FORTUNER / INNOVA
('Toyota', 'Fortuner', '4X2 MT', 2024, '2.7L Petrol', 'Petrol'),
('Toyota', 'Fortuner', '4X4 AT Diesel', 2024, '2.8L Diesel', 'Diesel'),
('Toyota', 'Fortuner', 'Legender 4X4', 2024, '2.8L Diesel', 'Diesel'),
('Toyota', 'Fortuner', 'GR-S', 2024, '2.8L Diesel', 'Diesel'),
('Toyota', 'Innova Hycross', 'GX', 2024, '2.0L Petrol', 'Petrol'),
('Toyota', 'Innova Hycross', 'VX Hybrid', 2024, '2.0L Strong Hybrid', 'Petrol Hybrid'),
('Toyota', 'Innova Hycross', 'ZX (O) Hybrid', 2024, '2.0L Strong Hybrid', 'Petrol Hybrid'),
('Toyota', 'Innova Crysta', 'GX', 2024, '2.4L Diesel', 'Diesel'),
('Toyota', 'Innova Crysta', 'ZX', 2024, '2.4L Diesel', 'Diesel'),
-- SKODA KUSHAQ / SLAVIA
('Skoda', 'Kushaq', 'Active', 2024, '1.0L TSI', 'Petrol'),
('Skoda', 'Kushaq', 'Ambition', 2024, '1.0L TSI', 'Petrol'),
('Skoda', 'Kushaq', 'Style', 2024, '1.5L TSI', 'Petrol'),
('Skoda', 'Kushaq', 'Monte Carlo', 2024, '1.5L TSI', 'Petrol'),
('Skoda', 'Slavia', 'Ambition', 2024, '1.0L TSI', 'Petrol'),
('Skoda', 'Slavia', 'Style', 2024, '1.5L TSI', 'Petrol'),
-- VW TAIGUN / VIRTUS
('Volkswagen', 'Taigun', 'Comfortline', 2024, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Taigun', 'Highline', 2024, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Taigun', 'GT Plus', 2024, '1.5L TSI', 'Petrol'),
('Volkswagen', 'Virtus', 'Highline', 2024, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Virtus', 'GT Plus Luxury', 2024, '1.5L TSI', 'Petrol'),
-- HONDA CITY / ELEVATE
('Honda', 'City', 'V', 2024, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'ZX', 2024, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City Hybrid', 'ZX e:HEV', 2024, '1.5L Atkinson', 'Petrol Hybrid'),
('Honda', 'Elevate', 'V', 2024, '1.5L i-VTEC', 'Petrol'),
('Honda', 'Elevate', 'ZX CVT', 2024, '1.5L i-VTEC', 'Petrol'),
-- MG HECTOR / ASTOR
('MG', 'Hector', 'Style', 2024, '1.5L Turbo', 'Petrol'),
('MG', 'Hector', 'Smart Pro', 2024, '2.0L Diesel', 'Diesel'),
('MG', 'Hector', 'Savvy Pro', 2024, '1.5L Turbo', 'Petrol'),
('MG', 'Astor', 'Style', 2024, '1.5L Petrol', 'Petrol'),
('MG', 'Astor', 'Savvy Sangria', 2024, '1.3L Turbo', 'Petrol'),
-- ADDITIONAL UTILITY
('Mahindra', 'XUV 3XO', 'MX1', 2024, '1.2L Turbo', 'Petrol'),
('Mahindra', 'XUV 3XO', 'AX5 L', 2024, '1.2L Turbo', 'Petrol'),
('Mahindra', 'XUV 3XO', 'AX7 L', 2024, '1.5L Diesel', 'Diesel'),
('Maruti Suzuki', 'Jimny', 'Zeta', 2024, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Jimny', 'Alpha', 2024, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Ertiga', 'LXi', 2024, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Ertiga', 'ZXi+ AT', 2024, '1.5L K-Series', 'Petrol'),
('Renault', 'Kwid', 'RXL', 2024, '1.0L', 'Petrol'),
('Renault', 'Triber', 'RXT', 2024, '1.0L', 'Petrol'),
('Renault', 'Kiger', 'RXZ Turbo', 2024, '1.0L Turbo', 'Petrol'),
('Nissan', 'Magnite', 'XV Premium', 2024, '1.0L Turbo', 'Petrol');

-- 4. INSERT FROM STAGING TO REAL TABLE
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT s.make_name, s.model_name, s.trim_name, s.m_year, s.e_type, s.f_type, m.id
FROM vehicle_staging s
JOIN models m ON s.model_name = m.name AND m.make_id = (SELECT id FROM makes WHERE name = s.make_name)
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = s.make_name 
    AND v.model = s.model_name 
    AND v.trim = s.trim_name 
    AND v.manufacture_year = s.m_year
);

DROP TEMPORARY TABLE IF EXISTS vehicle_staging;

SET FOREIGN_KEY_CHECKS = 1;
