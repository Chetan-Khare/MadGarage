-- V14: Comprehensive Vehicle Catalog Expansion (2021-2025)
-- Consolidates all modern Indian market variants into a single migration script.
-- Includes Tata, Mahindra, Maruti, Hyundai, Kia, Toyota, Ford, Isuzu, Force, 
-- and Luxury/EV segments (BYD, Mercedes, BMW, Audi, Volvo, Land Rover, Lexus, Porsche).

SET FOREIGN_KEY_CHECKS = 0;

-- 1. SEED ADDITIONAL MAKES
INSERT IGNORE INTO makes (name) VALUES 
('Ford'), ('Isuzu'), ('Force'), ('BYD'), 
('Mercedes-Benz'), ('BMW'), ('Audi'), 
('Volvo'), ('Land Rover'), ('Lexus'), ('Porsche');

-- 2. SEED ADDITIONAL MODELS
-- FORD
INSERT IGNORE INTO models (name, make_id) SELECT 'EcoSport', id FROM makes WHERE name = 'Ford';
INSERT IGNORE INTO models (name, make_id) SELECT 'Endeavour', id FROM makes WHERE name = 'Ford';
INSERT IGNORE INTO models (name, make_id) SELECT 'Figo', id FROM makes WHERE name = 'Ford';
INSERT IGNORE INTO models (name, make_id) SELECT 'Aspire', id FROM makes WHERE name = 'Ford';

-- SKODA
INSERT IGNORE INTO models (name, make_id) SELECT 'Octavia', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Superb', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Kodiaq', id FROM makes WHERE name = 'Skoda';

-- HONDA
INSERT IGNORE INTO models (name, make_id) SELECT 'Amaze', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Jazz', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'WR-V', id FROM makes WHERE name = 'Honda';

-- TATA
INSERT IGNORE INTO models (name, make_id) SELECT 'Tigor', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Punch.ev', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Nexon.ev', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Curvv', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Curvv.ev', id FROM makes WHERE name = 'Tata';

-- MAHINDRA
INSERT IGNORE INTO models (name, make_id) SELECT 'XUV500', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Marazzo', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'XUV300', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'KUV100 NXT', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'TUV300', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Alturas G4', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'XUV400 EV', id FROM makes WHERE name = 'Mahindra';

-- HYUNDAI
INSERT IGNORE INTO models (name, make_id) SELECT 'Aura', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Grand i10 Nios', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Ioniq 5', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Alcazar', id FROM makes WHERE name = 'Hyundai';

-- KIA
INSERT IGNORE INTO models (name, make_id) SELECT 'EV6', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Carens', id FROM makes WHERE name = 'Kia';

-- TOYOTA
INSERT IGNORE INTO models (name, make_id) SELECT 'Glanza', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Hilux', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Camry', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Urban Cruiser Hyryder', id FROM makes WHERE name = 'Toyota';

-- MARUTI SUZUKI
INSERT IGNORE INTO models (name, make_id) SELECT 'Invicto', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'XL6', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Alto K10', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'WagonR', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Celerio', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Eeco', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Dzire', id FROM makes WHERE name = 'Maruti Suzuki';

-- BYD
INSERT IGNORE INTO models (name, make_id) SELECT 'Atto 3', id FROM makes WHERE name = 'BYD';
INSERT IGNORE INTO models (name, make_id) SELECT 'Seal', id FROM makes WHERE name = 'BYD';
INSERT IGNORE INTO models (name, make_id) SELECT 'e6', id FROM makes WHERE name = 'BYD';

-- LUXURY BRANDS
-- MERCEDES
INSERT IGNORE INTO models (name, make_id) SELECT 'GLC', id FROM makes WHERE name = 'Mercedes-Benz';
INSERT IGNORE INTO models (name, make_id) SELECT 'C-Class', id FROM makes WHERE name = 'Mercedes-Benz';
INSERT IGNORE INTO models (name, make_id) SELECT 'E-Class', id FROM makes WHERE name = 'Mercedes-Benz';
INSERT IGNORE INTO models (name, make_id) SELECT 'GLA', id FROM makes WHERE name = 'Mercedes-Benz';
INSERT IGNORE INTO models (name, make_id) SELECT 'A-Class', id FROM makes WHERE name = 'Mercedes-Benz';

-- BMW
INSERT IGNORE INTO models (name, make_id) SELECT '3 Series', id FROM makes WHERE name = 'BMW';
INSERT IGNORE INTO models (name, make_id) SELECT 'X1', id FROM makes WHERE name = 'BMW';
INSERT IGNORE INTO models (name, make_id) SELECT 'X3', id FROM makes WHERE name = 'BMW';
INSERT IGNORE INTO models (name, make_id) SELECT 'X5', id FROM makes WHERE name = 'BMW';
INSERT IGNORE INTO models (name, make_id) SELECT '2 Series GC', id FROM makes WHERE name = 'BMW';

-- AUDI
INSERT IGNORE INTO models (name, make_id) SELECT 'A4', id FROM makes WHERE name = 'Audi';
INSERT IGNORE INTO models (name, make_id) SELECT 'Q3', id FROM makes WHERE name = 'Audi';
INSERT IGNORE INTO models (name, make_id) SELECT 'Q5', id FROM makes WHERE name = 'Audi';
INSERT IGNORE INTO models (name, make_id) SELECT 'Q7', id FROM makes WHERE name = 'Audi';
INSERT IGNORE INTO models (name, make_id) SELECT 'A6', id FROM makes WHERE name = 'Audi';

-- VOLVO
INSERT IGNORE INTO models (name, make_id) SELECT 'XC40', id FROM makes WHERE name = 'Volvo';
INSERT IGNORE INTO models (name, make_id) SELECT 'XC60', id FROM makes WHERE name = 'Volvo';
INSERT IGNORE INTO models (name, make_id) SELECT 'XC90', id FROM makes WHERE name = 'Volvo';
INSERT IGNORE INTO models (name, make_id) SELECT 'S60', id FROM makes WHERE name = 'Volvo';

-- LAND ROVER
INSERT IGNORE INTO models (name, make_id) SELECT 'Defender', id FROM makes WHERE name = 'Land Rover';
INSERT IGNORE INTO models (name, make_id) SELECT 'Range Rover Evoque', id FROM makes WHERE name = 'Land Rover';

-- LEXUS
INSERT IGNORE INTO models (name, make_id) SELECT 'NX', id FROM makes WHERE name = 'Lexus';
INSERT IGNORE INTO models (name, make_id) SELECT 'RX', id FROM makes WHERE name = 'Lexus';
INSERT IGNORE INTO models (name, make_id) SELECT 'ES', id FROM makes WHERE name = 'Lexus';
INSERT IGNORE INTO models (name, make_id) SELECT 'LC', id FROM makes WHERE name = 'Lexus';

-- PORSCHE
INSERT IGNORE INTO models (name, make_id) SELECT 'Macan', id FROM makes WHERE name = 'Porsche';
INSERT IGNORE INTO models (name, make_id) SELECT 'Cayenne', id FROM makes WHERE name = 'Porsche';
INSERT IGNORE INTO models (name, make_id) SELECT 'Taycan', id FROM makes WHERE name = 'Porsche';

-- MG, CITROEN, RENAULT, NISSAN
INSERT IGNORE INTO models (name, make_id) SELECT 'Windsor EV', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'Comet EV', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'ZS EV', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'Gloster', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'C3', id FROM makes WHERE name = 'Citroen';
INSERT IGNORE INTO models (name, make_id) SELECT 'C3 Aircross', id FROM makes WHERE name = 'Citroen';
INSERT IGNORE INTO models (name, make_id) SELECT 'Kwid', id FROM makes WHERE name = 'Renault';
INSERT IGNORE INTO models (name, make_id) SELECT 'Triber', id FROM makes WHERE name = 'Renault';
INSERT IGNORE INTO models (name, make_id) SELECT 'Kiger', id FROM makes WHERE name = 'Renault';
INSERT IGNORE INTO models (name, make_id) SELECT 'Magnite', id FROM makes WHERE name = 'Nissan';

-- ISUZU & FORCE
-- Note: V-Cross is a trim of D-Max, not a separate model
INSERT IGNORE INTO models (name, make_id) SELECT 'D-Max', id FROM makes WHERE name = 'Isuzu';
INSERT IGNORE INTO models (name, make_id) SELECT 'mu-X', id FROM makes WHERE name = 'Isuzu';
INSERT IGNORE INTO models (name, make_id) SELECT 'Gurkha', id FROM makes WHERE name = 'Force';

-- 3. BULK SEED VARIANTS (2021-2025)
CREATE TEMPORARY TABLE IF NOT EXISTS vehicle_composite_staging (
    make_name VARCHAR(100),
    model_name VARCHAR(100),
    trim_name VARCHAR(100),
    m_year INT,
    e_type VARCHAR(100),
    f_type VARCHAR(50)
);

INSERT INTO vehicle_composite_staging (make_name, model_name, trim_name, m_year, e_type, f_type) VALUES 
-- ============================================================
-- TATA (2021-2025)
-- ============================================================
('Tata', 'Nexon', 'XE', 2021, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'XM', 2021, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'XZ+', 2021, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'XM(S)', 2022, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'XZ+ Lux', 2022, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'Smart', 2023, '1.2L Turbo', 'Petrol'),
('Tata', 'Nexon', 'Creative', 2023, '1.5L Diesel', 'Diesel'),
('Tata', 'Nexon', 'Fearless+ S', 2024, '1.2L Turbo GDi', 'Petrol'),
('Tata', 'Nexon', 'iCNG', 2025, '1.2L Turbo', 'CNG'),
('Tata', 'Nexon.ev', 'Empowered', 2022, 'Electric', 'Electric'),
('Tata', 'Nexon.ev', 'Empowered+', 2023, 'Electric', 'Electric'),
('Tata', 'Nexon.ev', 'Empowered+', 2025, 'Electric', 'Electric'),

('Tata', 'Punch', 'Pure', 2021, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Adventure', 2021, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Accomplished', 2022, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Creative', 2023, '1.2L Revotron', 'Petrol'),
('Tata', 'Punch', 'Adventure iCNG', 2024, '1.2L Revotron', 'CNG'),
('Tata', 'Punch.ev', 'Empowered', 2025, 'Electric', 'Electric'),

('Tata', 'Harrier', 'XE', 2021, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'XTA+', 2022, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Pure+', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Adventure+', 2024, '2.0L Kryotec', 'Diesel'),
('Tata', 'Harrier', 'Fearless+', 2025, '2.0L Kryotec', 'Diesel'),

('Tata', 'Safari', 'XE', 2021, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'XZ+', 2022, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Pure+', 2023, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Accomplished', 2024, '2.0L Kryotec', 'Diesel'),
('Tata', 'Safari', 'Empowered+', 2025, '2.0L Kryotec', 'Diesel'),

('Tata', 'Altroz', 'XE', 2021, '1.2L Revotron', 'Petrol'),
('Tata', 'Altroz', 'XZ+ iTurbo', 2021, '1.2L Turbo', 'Petrol'),
('Tata', 'Altroz', 'XM+', 2022, '1.5L Diesel', 'Diesel'),
('Tata', 'Altroz', 'XT iCNG', 2023, '1.2L Petrol', 'CNG'),
('Tata', 'Altroz', 'XZ+ Luxe', 2024, '1.2L Turbo', 'Petrol'),
('Tata', 'Altroz Racer', 'R3', 2025, '1.2L Turbo', 'Petrol'),

('Tata', 'Tiago', 'XE', 2021, '1.2L Revotron', 'Petrol'),
('Tata', 'Tiago', 'XZ+', 2022, '1.2L Revotron', 'Petrol'),
('Tata', 'Tiago', 'XT iCNG', 2023, '1.2L Revotron', 'CNG'),
('Tata', 'Tiago', 'XZ+ iCNG', 2024, '1.2L Revotron', 'CNG'),

('Tata', 'Curvv', 'Creative', 2024, '1.2L Turbo GDi', 'Petrol'),
('Tata', 'Curvv', 'Accomplished', 2024, '1.5L Kryojet Diesel', 'Diesel'),
('Tata', 'Curvv.ev', 'Empowered+', 2024, 'Electric', 'Electric'),

-- ============================================================
-- MAHINDRA (2021-2025)
-- ============================================================
('Mahindra', 'XUV700', 'AX3', 2021, '2.0L mStallion', 'Petrol'),
('Mahindra', 'XUV700', 'AX5', 2022, '2.2L mHawk', 'Diesel'),
('Mahindra', 'XUV700', 'AX7 L', 2023, '2.2L mHawk', 'Diesel'),
('Mahindra', 'XUV700', 'AX7 L AWD', 2025, '2.2L mHawk', 'Diesel'),

('Mahindra', 'Scorpio-N', 'Z2', 2022, '2.0L mStallion', 'Petrol'),
('Mahindra', 'Scorpio-N', 'Z4', 2022, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z8 L', 2023, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio-N', 'Z8 L AWD', 2025, '2.2L mHawk', 'Diesel'),

('Mahindra', 'Thar', 'LX 4WD AT', 2021, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar', 'LX Hard Top', 2022, '2.0L mStallion', 'Petrol'),
('Mahindra', 'Thar', 'LX 4WD', 2023, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar Roxx', 'AX5 L', 2024, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Thar Roxx', 'AX7 L 4WD', 2025, '2.2L mHawk', 'Diesel'),

('Mahindra', 'XUV300', 'W4', 2021, '1.2L mStallion', 'Petrol'),
('Mahindra', 'XUV300', 'W6', 2022, '1.5L Diesel', 'Diesel'),
('Mahindra', 'XUV300', 'W8 (O)', 2023, '1.5L Diesel', 'Diesel'),

('Mahindra', 'XUV400 EV', 'EC', 2023, 'Electric', 'Electric'),
('Mahindra', 'XUV400 EV', 'EL Pro', 2025, 'Electric', 'Electric'),

-- ============================================================
-- MARUTI SUZUKI (2021-2025)
-- ============================================================
('Maruti Suzuki', 'Swift', 'LXi', 2021, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2022, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi', 2023, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+', 2024, '1.2L Z-Series', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+ AMT', 2025, '1.2L Z-Series', 'Petrol'),

('Maruti Suzuki', 'WagonR', 'LXi', 2021, '1.0L K10', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi', 2022, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'ZXi+', 2023, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi CNG', 2024, '1.0L K10', 'CNG'),
('Maruti Suzuki', 'WagonR', 'ZXi', 2025, '1.2L K12', 'Petrol'),

('Maruti Suzuki', 'Dzire', 'LXi', 2021, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Dzire', 'VXi', 2022, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Dzire', 'ZXi', 2023, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Dzire', 'VXi CNG', 2024, '1.2L K12', 'CNG'),
('Maruti Suzuki', 'Dzire', 'ZXi+', 2025, '1.2L Z-Series', 'Petrol'),

('Maruti Suzuki', 'Baleno', 'Sigma', 2021, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Alpha', 2022, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Alpha MT', 2023, '1.2L DualJet', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Alpha CNG', 2024, '1.2L DualJet', 'CNG'),

('Maruti Suzuki', 'Brezza', 'LXi', 2021, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Brezza', 'VXi', 2022, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Brezza', 'ZXi+', 2023, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Brezza', 'ZXi+ Dual Tone', 2024, '1.5L K-Series', 'Petrol'),

('Maruti Suzuki', 'Ertiga', 'VXi', 2021, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Ertiga', 'ZXi+', 2022, '1.5L K-Series', 'Petrol'),
('Maruti Suzuki', 'Ertiga', 'VXi CNG', 2023, '1.5L K-Series', 'CNG'),
('Maruti Suzuki', 'Ertiga', 'ZXi+ CNG', 2024, '1.5L K-Series', 'CNG'),

('Maruti Suzuki', 'Grand Vitara', 'Sigma', 2022, '1.5L Mild Hybrid', 'Petrol'),
('Maruti Suzuki', 'Grand Vitara', 'Alpha+', 2023, '1.5L Strong Hybrid', 'Petrol Hybrid'),
('Maruti Suzuki', 'Grand Vitara', 'Zeta+', 2024, '1.5L Mild Hybrid', 'Petrol'),

('Maruti Suzuki', 'Alto K10', 'VXi', 2022, '1.0L K10C', 'Petrol'),
('Maruti Suzuki', 'Alto K10', 'VXi+', 2023, '1.0L K10C', 'Petrol'),
('Maruti Suzuki', 'Alto K10', 'VXi CNG', 2024, '1.0L K10C', 'CNG'),

-- ============================================================
-- HYUNDAI (2021-2025)
-- ============================================================
('Hyundai', 'Creta', 'S', 2021, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'SX', 2022, '1.5L MPi', 'Petrol'),
('Hyundai', 'Creta', 'SX (O)', 2023, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'SX (O) DCT', 2025, '1.5L Turbo', 'Petrol'),

('Hyundai', 'Venue', 'S', 2021, '1.2L MPi', 'Petrol'),
('Hyundai', 'Venue', 'SX', 2022, '1.0L Turbo', 'Petrol'),
('Hyundai', 'Venue', 'SX (O)', 2023, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Venue', 'SX (O) DCT', 2024, '1.0L Turbo', 'Petrol'),

('Hyundai', 'i20', 'Magna', 2021, '1.2L MPi', 'Petrol'),
('Hyundai', 'i20', 'Sportz', 2022, '1.0L Turbo', 'Petrol'),
('Hyundai', 'i20', 'Asta (O)', 2023, '1.0L Turbo', 'Petrol'),
('Hyundai', 'i20 N Line', 'N8', 2024, '1.0L Turbo', 'Petrol'),

('Hyundai', 'Verna', 'S', 2021, '1.5L MPi', 'Petrol'),
('Hyundai', 'Verna', 'SX (O)', 2022, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Verna', 'SX (O) Turbo', 2023, '1.5L Turbo GDi', 'Petrol'),
('Hyundai', 'Verna', 'SX (O) IVT', 2024, '1.5L MPi', 'Petrol'),

('Hyundai', 'Alcazar', 'Prestige', 2021, '2.0L MPi', 'Petrol'),
('Hyundai', 'Alcazar', 'Platinum', 2022, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Alcazar', 'Signature', 2023, '1.5L Turbo', 'Petrol'),
('Hyundai', 'Alcazar', 'Signature (O)', 2024, '1.5L Turbo', 'Petrol'),

('Hyundai', 'Grand i10 Nios', 'Magna', 2021, '1.2L MPi', 'Petrol'),
('Hyundai', 'Grand i10 Nios', 'Sportz CNG', 2022, '1.2L MPi', 'CNG'),
('Hyundai', 'Grand i10 Nios', 'Asta', 2023, '1.2L MPi', 'Petrol'),

('Hyundai', 'Ioniq 5', 'RWD', 2023, 'Electric', 'Electric'),
('Hyundai', 'Ioniq 5', 'AWD', 2024, 'Electric', 'Electric'),

-- ============================================================
-- KIA (2021-2025)
-- ============================================================
('Kia', 'Seltos', 'HTX', 2021, '1.5L MPi', 'Petrol'),
('Kia', 'Seltos', 'HTX+', 2022, '1.4L Turbo', 'Petrol'),
('Kia', 'Seltos', 'X-Line', 2023, '1.5L Turbo', 'Petrol'),
('Kia', 'Seltos', 'GTX+ Turbo', 2024, '1.5L Turbo GDi', 'Petrol'),

('Kia', 'Sonet', 'HTE', 2021, '1.2L MPi', 'Petrol'),
('Kia', 'Sonet', 'HTX', 2022, '1.0L Turbo', 'Petrol'),
('Kia', 'Sonet', 'HTX+', 2023, '1.5L CRDi', 'Diesel'),
('Kia', 'Sonet', 'X-Line', 2024, '1.5L CRDi', 'Diesel'),

('Kia', 'Carens', 'Premium', 2022, '1.5L MPi', 'Petrol'),
('Kia', 'Carens', 'Prestige Plus', 2023, '1.5L CRDi', 'Diesel'),
('Kia', 'Carens', 'Luxury Plus', 2024, '1.5L Turbo', 'Petrol'),

('Kia', 'EV6', 'GT-Line RWD', 2022, 'Electric', 'Electric'),
('Kia', 'EV6', 'GT-Line AWD', 2023, 'Electric', 'Electric'),

-- ============================================================
-- TOYOTA (2021-2025)
-- ============================================================
('Toyota', 'Fortuner', 'Standard', 2021, '2.7L Petrol', 'Petrol'),
('Toyota', 'Fortuner', 'Legender', 2022, '2.8L GD-6', 'Diesel'),
('Toyota', 'Fortuner', 'GR Sport', 2023, '2.8L GD-6', 'Diesel'),
('Toyota', 'Fortuner', 'GR-S 4X4', 2024, '2.8L GD-6', 'Diesel'),

('Toyota', 'Innova Hycross', 'G MT', 2022, '2.0L Petrol', 'Petrol'),
('Toyota', 'Innova Hycross', 'VX HV', 2023, '2.0L Hybrid', 'Petrol Hybrid'),
('Toyota', 'Innova Hycross', 'ZX (O) HV', 2024, '2.0L Hybrid', 'Petrol Hybrid'),

('Toyota', 'Innova Crysta', 'G MT', 2021, '2.4L Diesel', 'Diesel'),
('Toyota', 'Innova Crysta', 'VX AT', 2022, '2.4L Diesel', 'Diesel'),
('Toyota', 'Innova Crysta', 'ZX AT', 2023, '2.4L Diesel', 'Diesel'),

('Toyota', 'Urban Cruiser Hyryder', 'E', 2022, '1.5L NeoDrive', 'Petrol'),
('Toyota', 'Urban Cruiser Hyryder', 'G', 2023, '1.5L Strong Hybrid', 'Petrol Hybrid'),
('Toyota', 'Urban Cruiser Hyryder', 'V', 2024, '1.5L Strong Hybrid', 'Petrol Hybrid'),

('Toyota', 'Glanza', 'E', 2022, '1.2L DualJet', 'Petrol'),
('Toyota', 'Glanza', 'G', 2023, '1.2L DualJet', 'Petrol'),
('Toyota', 'Glanza', 'V', 2024, '1.2L DualJet', 'Petrol'),

-- ============================================================
-- HONDA (2021-2025)
-- ============================================================
('Honda', 'City', 'S', 2021, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'V', 2022, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'ZX CVT', 2023, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'ZX CVT', 2024, '1.5L i-VTEC', 'Petrol'),

('Honda', 'Amaze', 'S', 2021, '1.2L i-VTEC', 'Petrol'),
('Honda', 'Amaze', 'VX', 2022, '1.2L i-VTEC', 'Petrol'),
('Honda', 'Amaze', 'ZX CVT', 2023, '1.2L i-VTEC', 'Petrol'),
('Honda', 'Elevate', 'SV', 2023, '1.5L i-VTEC', 'Petrol'),
('Honda', 'Elevate', 'ZX CVT', 2024, '1.5L i-VTEC', 'Petrol'),

-- ============================================================
-- SKODA / VW (2021-2025)
-- ============================================================
('Skoda', 'Kushaq', 'Active', 2021, '1.0L TSI', 'Petrol'),
('Skoda', 'Kushaq', 'Style', 2022, '1.5L TSI', 'Petrol'),
('Skoda', 'Kushaq', 'Monte Carlo', 2023, '1.5L TSI', 'Petrol'),
('Skoda', 'Slavia', 'Active', 2022, '1.0L TSI', 'Petrol'),
('Skoda', 'Slavia', 'Style', 2023, '1.5L TSI', 'Petrol'),
('Skoda', 'Slavia', 'Prestige', 2025, '1.5L TSI', 'Petrol'),
('Volkswagen', 'Taigun', 'Trendline', 2021, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Taigun', 'Topline', 2022, '1.5L TSI', 'Petrol'),
('Volkswagen', 'Virtus', 'Comfortline', 2022, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Virtus', 'GT', 2023, '1.5L TSI', 'Petrol'),

-- ============================================================
-- FORD (2021 only - discontinued India 2021)
-- ============================================================
('Ford', 'EcoSport', 'Titanium', 2021, '1.5L Ti-VCT', 'Petrol'),
('Ford', 'EcoSport', 'S', 2021, '1.5L TDCi', 'Diesel'),
('Ford', 'Endeavour', 'Sport', 2021, '2.0L EcoBlue', 'Diesel'),
('Ford', 'Figo', 'Titanium Blu', 2021, '1.2L Ti-VCT', 'Petrol'),

-- ============================================================
-- MG (2021-2025)
-- ============================================================
('MG', 'Hector', 'Style', 2021, '1.5L Turbo', 'Petrol'),
('MG', 'Hector', 'Smart', 2022, '1.5L Turbo', 'Petrol'),
('MG', 'Hector', 'Sharp Pro', 2023, '1.5L Turbo', 'Petrol'),
('MG', 'Hector', 'Savvy Pro', 2024, '1.5L Turbo', 'Petrol'),
('MG', 'ZS EV', 'Excite', 2021, 'Electric', 'Electric'),
('MG', 'ZS EV', 'Exclusive', 2022, 'Electric', 'Electric'),
('MG', 'ZS EV', 'Exclusive', 2023, 'Electric', 'Electric'),
('MG', 'Astor', 'Style', 2021, '1.5L MPi', 'Petrol'),
('MG', 'Astor', 'Sharp', 2022, '1.5L MPi', 'Petrol'),
('MG', 'Astor', 'Savvy', 2023, '1.4L Turbo', 'Petrol'),
('MG', 'Windsor EV', 'Exclusive', 2024, 'Electric', 'Electric'),
('MG', 'Gloster', 'Savvy 4x4', 2024, '2.0L Twin Turbo', 'Diesel'),

-- ============================================================
-- RENAULT / NISSAN (2021-2025)
-- ============================================================
('Renault', 'Kiger', 'RXE', 2021, '1.0L NA', 'Petrol'),
('Renault', 'Kiger', 'RXT', 2022, '1.0L Turbo', 'Petrol'),
('Renault', 'Kiger', 'RXZ Turbo', 2023, '1.0L Turbo', 'Petrol'),
('Renault', 'Kiger', 'RXZ Turbo', 2025, '1.0L Turbo', 'Petrol'),
('Renault', 'Kwid', 'RXE', 2021, '1.0L SCe', 'Petrol'),
('Renault', 'Kwid', 'RXL', 2023, '1.0L SCe', 'Petrol'),
('Renault', 'Triber', 'RXE', 2021, '1.0L Energy', 'Petrol'),
('Renault', 'Triber', 'RXT', 2022, '1.0L Energy', 'Petrol'),
('Renault', 'Triber', 'RXZ', 2023, '1.0L Energy', 'Petrol'),
('Nissan', 'Magnite', 'XE', 2021, '1.0L NA', 'Petrol'),
('Nissan', 'Magnite', 'XV', 2022, '1.0L Turbo', 'Petrol'),
('Nissan', 'Magnite', 'XV Premium', 2023, '1.0L Turbo', 'Petrol'),
('Nissan', 'Magnite', 'XV Premium', 2024, '1.0L Turbo', 'Petrol'),

-- ============================================================
-- ISUZU & FORCE (2021-2025)
-- ============================================================
('Isuzu', 'D-Max', 'V-Cross High', 2021, '1.9L Diesel', 'Diesel'),
('Isuzu', 'D-Max', 'V-Cross Z', 2022, '1.9L Diesel', 'Diesel'),
('Isuzu', 'D-Max', 'V-Cross Z', 2024, '1.9L Diesel', 'Diesel'),
('Isuzu', 'mu-X', '4x4 MT', 2022, '1.9L Diesel', 'Diesel'),
('Isuzu', 'mu-X', '4x4 AT', 2024, '1.9L Diesel', 'Diesel'),
('Force', 'Gurkha', '3-Door 4x4', 2021, '2.6L Diesel', 'Diesel'),
('Force', 'Gurkha', '3-Door 4x4', 2023, '2.6L Diesel', 'Diesel'),
('Force', 'Gurkha', '5-Door 4x4', 2024, '2.6L Diesel', 'Diesel'),

-- ============================================================
-- BYD
-- ============================================================
('BYD', 'Atto 3', 'Standard', 2023, 'Electric', 'Electric'),
('BYD', 'Atto 3', 'Superior', 2024, 'Electric', 'Electric'),
('BYD', 'Seal', 'Performance AWD', 2025, 'Electric', 'Electric'),
('BYD', 'e6', 'GL', 2023, 'Electric', 'Electric'),

-- ============================================================
-- LUXURY (2021-2025) - Key variants per model
-- ============================================================
('Mercedes-Benz', 'C-Class', 'C200', 2021, '1.5L Turbo', 'Petrol'),
('Mercedes-Benz', 'C-Class', 'C220d', 2022, '2.0L Diesel', 'Diesel'),
('Mercedes-Benz', 'C-Class', 'C200', 2023, '1.5L Turbo', 'Petrol'),
('Mercedes-Benz', 'GLC', '300 4MATIC', 2022, '2.0L Turbo', 'Petrol'),
('Mercedes-Benz', 'GLC', '300 4MATIC', 2024, '2.0L Turbo', 'Petrol'),
('Mercedes-Benz', 'E-Class', 'E200', 2022, '2.0L Turbo', 'Petrol'),
('Mercedes-Benz', 'E-Class', 'E200', 2024, '2.0L Turbo', 'Petrol'),
('Mercedes-Benz', 'GLA', '200', 2021, '1.3L Turbo', 'Petrol'),
('Mercedes-Benz', 'GLA', '200', 2023, '1.3L Turbo', 'Petrol'),
('Mercedes-Benz', 'A-Class', 'A200', 2021, '1.3L Turbo', 'Petrol'),
('Mercedes-Benz', 'A-Class', 'A200', 2022, '1.3L Turbo', 'Petrol'),

('BMW', '3 Series', '320Li Sport', 2021, '2.0L TwinPower Turbo', 'Petrol'),
('BMW', '3 Series', '330Li M Sport', 2022, '2.0L TwinPower Turbo', 'Petrol'),
('BMW', '3 Series', '330Li M Sport', 2024, '2.0L TwinPower Turbo', 'Petrol'),
('BMW', 'X1', 'sDrive18i xLine', 2022, '1.5L TwinPower Turbo', 'Petrol'),
('BMW', 'X1', 'sDrive18i M Sport', 2023, '1.5L TwinPower Turbo', 'Petrol'),
('BMW', 'X3', 'xDrive20d Luxury', 2022, '2.0L TwinPower Turbo', 'Diesel'),
('BMW', 'X3', 'xDrive20d', 2024, '2.0L TwinPower Turbo', 'Diesel'),
('BMW', 'X5', 'xDrive40i M Sport', 2023, '3.0L TwinPower Turbo', 'Petrol'),
('BMW', 'X5', 'xDrive40i M Sport', 2024, '3.0L TwinPower Turbo', 'Petrol'),
('BMW', '2 Series GC', '220i M Sport', 2022, '2.0L TwinPower Turbo', 'Petrol'),
('BMW', '2 Series GC', '220i M Sport', 2023, '2.0L TwinPower Turbo', 'Petrol'),

('Audi', 'A4', '40 TFSI Technology', 2021, '2.0L Turbo', 'Petrol'),
('Audi', 'A4', '40 TFSI Technology', 2023, '2.0L Turbo', 'Petrol'),
('Audi', 'Q3', '40 TFSI Technology', 2021, '2.0L Turbo', 'Petrol'),
('Audi', 'Q3', '40 TFSI Quattro', 2024, '2.0L Turbo', 'Petrol'),
('Audi', 'Q5', '45 TFSI Technology', 2022, '2.0L Turbo', 'Petrol'),
('Audi', 'Q5', '45 TFSI Technology', 2023, '2.0L Turbo', 'Petrol'),
('Audi', 'Q7', '55 TFSI Technology', 2023, '3.0L Turbo', 'Petrol'),
('Audi', 'Q7', '55 TFSI Technology', 2024, '3.0L Turbo', 'Petrol'),
('Audi', 'A6', '45 TFSI Technology', 2022, '2.0L Turbo', 'Petrol'),
('Audi', 'A6', '45 TFSI Technology', 2023, '2.0L Turbo', 'Petrol'),

('Volvo', 'XC40', 'R-Design', 2021, '2.0L Turbo', 'Petrol'),
('Volvo', 'XC40', 'B4 Ultimate', 2023, '2.0L Turbo', 'Petrol Hybrid'),
('Volvo', 'XC60', 'T5', 2021, '2.0L Turbo', 'Petrol'),
('Volvo', 'XC60', 'B5 Ultimate', 2024, '2.0L Turbo', 'Petrol Hybrid'),
('Volvo', 'XC90', 'D5', 2021, '2.0L Diesel', 'Diesel'),
('Volvo', 'XC90', 'B6 Ultimate', 2024, '2.0L Turbo', 'Petrol Hybrid'),
('Volvo', 'S60', 'T4', 2021, '2.0L Turbo', 'Petrol'),
('Volvo', 'S60', 'T4 Inscription', 2022, '2.0L Turbo', 'Petrol'),

('Land Rover', 'Defender', '90 SE', 2021, '2.0L Petrol', 'Petrol'),
('Land Rover', 'Defender', '110 SE', 2022, '3.0L Diesel', 'Diesel'),
('Land Rover', 'Defender', '110 SE', 2024, '3.0L Diesel', 'Diesel'),
('Land Rover', 'Range Rover Evoque', 'S', 2021, '2.0L Diesel', 'Diesel'),
('Land Rover', 'Range Rover Evoque', 'SE', 2023, '2.0L Diesel', 'Diesel'),

('Lexus', 'NX', '300h', 2021, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'NX', '350h Luxury', 2023, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'NX', '350h Luxury', 2024, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'RX', '450h', 2022, '3.5L V6 Hybrid', 'Petrol Hybrid'),
('Lexus', 'RX', '350h Luxury', 2024, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'ES', '300h', 2021, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'ES', '300h Luxury', 2023, '2.5L Hybrid', 'Petrol Hybrid'),
('Lexus', 'LC', '500h', 2023, '3.5L V6 Hybrid', 'Petrol Hybrid'),
('Lexus', 'LC', '500h', 2024, '3.5L V6 Hybrid', 'Petrol Hybrid'),

('Porsche', 'Macan', 'Standard', 2021, '2.0L Turbo', 'Petrol'),
('Porsche', 'Macan', 'GTS', 2022, '2.9L Turbo', 'Petrol'),
('Porsche', 'Macan', 'Standard', 2023, '2.0L Turbo', 'Petrol'),
('Porsche', 'Cayenne', 'E-Hybrid', 2022, '3.0L PHEV', 'Petrol Hybrid'),
('Porsche', 'Cayenne', 'V6', 2024, '3.0L Turbo', 'Petrol'),
('Porsche', 'Taycan', '4S', 2022, 'Electric', 'Electric'),
('Porsche', 'Taycan', '4S', 2024, 'Electric', 'Electric');

-- 4. INSERT INTO MAIN TABLE
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT s.make_name, s.model_name, s.trim_name, s.m_year, s.e_type, s.f_type, m.id
FROM (SELECT DISTINCT * FROM vehicle_composite_staging) s
JOIN models m ON s.model_name = m.name AND m.make_id = (SELECT id FROM makes WHERE name = s.make_name)
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = s.make_name 
    AND v.model = s.model_name 
    AND v.trim = s.trim_name 
    AND v.manufacture_year = s.m_year
);

DROP TEMPORARY TABLE IF EXISTS vehicle_composite_staging;

SET FOREIGN_KEY_CHECKS = 1;
