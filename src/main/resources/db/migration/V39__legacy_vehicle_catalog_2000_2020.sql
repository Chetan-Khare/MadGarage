-- V39: Legacy Vehicle Catalog (2000-2020)
-- Adding iconic Indian market vehicles that were popular between 2000 and 2020.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. ENSURE ALL MAKES EXIST
INSERT IGNORE INTO makes (name) VALUES 
('Maruti Suzuki'), ('Hyundai'), ('Tata'), ('Mahindra'), 
('Toyota'), ('Honda'), ('Skoda'), ('Volkswagen'), 
('Ford'), ('Chevrolet'), ('Mitsubishi'), ('Fiat');

-- 2. SEED LEGACY MODELS
-- MARUTI
INSERT IGNORE INTO models (name, make_id) SELECT '800', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Zen', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Esteem', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Omni', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Gypsy', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'SX4', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Ritz', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Alto', id FROM makes WHERE name = 'Maruti Suzuki';

-- HYUNDAI
INSERT IGNORE INTO models (name, make_id) SELECT 'Santro', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Santro Xing', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Accent', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Getz', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'i10', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Eon', id FROM makes WHERE name = 'Hyundai';

-- TATA
INSERT IGNORE INTO models (name, make_id) SELECT 'Indica', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Indigo', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Nano', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Sumo', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Safari Storme', id FROM makes WHERE name = 'Tata';

-- MAHINDRA
INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Bolero', id FROM makes WHERE name = 'Mahindra';

-- TOYOTA
INSERT IGNORE INTO models (name, make_id) SELECT 'Qualis', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Corolla', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Corolla Altis', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Etios', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Innova', id FROM makes WHERE name = 'Toyota';

-- CHEVROLET / MITSUBISHI / FIAT
INSERT IGNORE INTO models (name, make_id) SELECT 'Beat', id FROM makes WHERE name = 'Chevrolet';
INSERT IGNORE INTO models (name, make_id) SELECT 'Cruze', id FROM makes WHERE name = 'Chevrolet';
INSERT IGNORE INTO models (name, make_id) SELECT 'Spark', id FROM makes WHERE name = 'Chevrolet';
INSERT IGNORE INTO models (name, make_id) SELECT 'Lancer', id FROM makes WHERE name = 'Mitsubishi';
INSERT IGNORE INTO models (name, make_id) SELECT 'Pajero', id FROM makes WHERE name = 'Mitsubishi';
INSERT IGNORE INTO models (name, make_id) SELECT 'Punto', id FROM makes WHERE name = 'Fiat';
INSERT IGNORE INTO models (name, make_id) SELECT 'Linea', id FROM makes WHERE name = 'Fiat';

-- FORD (Legacy)
INSERT IGNORE INTO models (name, make_id) SELECT 'Ikon', id FROM makes WHERE name = 'Ford';

-- SKODA (Legacy)
INSERT IGNORE INTO models (name, make_id) SELECT 'Laura', id FROM makes WHERE name = 'Skoda';

-- VOLKSWAGEN (Legacy)
INSERT IGNORE INTO models (name, make_id) SELECT 'Polo', id FROM makes WHERE name = 'Volkswagen';
INSERT IGNORE INTO models (name, make_id) SELECT 'Vento', id FROM makes WHERE name = 'Volkswagen';

-- 3. BULK SEED VARIANTS (2000-2020)
CREATE TEMPORARY TABLE IF NOT EXISTS legacy_staging (
    make_name VARCHAR(100),
    model_name VARCHAR(100),
    trim_name VARCHAR(100),
    m_year INT,
    e_type VARCHAR(100),
    f_type VARCHAR(50)
);

INSERT INTO legacy_staging (make_name, model_name, trim_name, m_year, e_type, f_type) VALUES 
-- MARUTI 800 & ZEN
('Maruti Suzuki', '800', 'Std', 2000, '796cc', 'Petrol'),
('Maruti Suzuki', '800', 'DX', 2005, '796cc', 'Petrol'),
('Maruti Suzuki', '800', 'AC', 2010, '796cc', 'Petrol'),
('Maruti Suzuki', 'Zen', 'LXi', 2003, '1.0L G10', 'Petrol'),
('Maruti Suzuki', 'Zen', 'VXi', 2005, '1.0L G10', 'Petrol'),
('Maruti Suzuki', 'Esteem', 'VXi', 2004, '1.3L G13', 'Petrol'),
('Maruti Suzuki', 'Esteem', 'LXi', 2006, '1.3L G13', 'Petrol'),
('Maruti Suzuki', 'Alto', 'LX', 2005, '796cc', 'Petrol'),
('Maruti Suzuki', 'Alto', 'LXi', 2012, '800cc', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'LXi', 2004, '1.1L F10D', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi', 2010, '1.0L K10', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2005, '1.3L G13', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VDi', 2008, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZXi', 2015, '1.2L K12', 'Petrol'),

-- HYUNDAI SANTRO & ACCENT
('Hyundai', 'Santro', 'LP', 2000, '1.0L Epsilon', 'Petrol'),
('Hyundai', 'Santro Xing', 'XP', 2004, '1.1L Epsilon', 'Petrol'),
('Hyundai', 'Santro Xing', 'GLS', 2010, '1.1L Epsilon', 'Petrol'),
('Hyundai', 'Accent', 'GVS', 2002, '1.5L G4EC', 'Petrol'),
('Hyundai', 'Accent', 'Executive', 2008, '1.5L G4EC', 'Petrol'),
('Hyundai', 'i10', 'Era', 2008, '1.1L iRDE', 'Petrol'),
('Hyundai', 'i10', 'Magna', 2011, '1.2L Kappa', 'Petrol'),
('Hyundai', 'Verna', 'VGT', 2007, '1.5L CRDi', 'Diesel'),
('Hyundai', 'Verna', 'Fluidic', 2012, '1.6L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'Base', 2015, '1.6L VTVT', 'Petrol'),
('Hyundai', 'Creta', 'SX', 2018, '1.6L CRDi', 'Diesel'),

-- HONDA CITY & CIVIC
('Honda', 'City', 'Type 1', 2000, '1.5L SOHC D15B', 'Petrol'),
('Honda', 'City', 'GXi', 2005, '1.5L i-DSI', 'Petrol'),
('Honda', 'City', 'VTEC', 2007, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'V', 2010, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'VX', 2014, '1.5L i-DTEC', 'Diesel'),
('Honda', 'Civic', 'V', 2007, '1.8L i-VTEC', 'Petrol'),
('Honda', 'Civic', 'Hybrid', 2009, '1.3L IMA', 'Petrol Hybrid'),

-- TOYOTA QUALIS & INNOVA
('Toyota', 'Qualis', 'FS', 2000, '2.4L Diesel', 'Diesel'),
('Toyota', 'Qualis', 'GST', 2003, '2.4L Diesel', 'Diesel'),
('Toyota', 'Innova', 'G', 2005, '2.5L D-4D', 'Diesel'),
('Toyota', 'Innova', 'V', 2009, '2.5L D-4D', 'Diesel'),
('Toyota', 'Innova Crysta', 'GX', 2016, '2.4L GD', 'Diesel'),
('Toyota', 'Corolla', 'H1', 2003, '1.8L VVT-i', 'Petrol'),
('Toyota', 'Corolla Altis', 'G', 2010, '1.8L Dual VVT-i', 'Petrol'),

-- TATA INDICA & SAFARI
('Tata', 'Indica', 'V2', 2002, '1.4L DL', 'Diesel'),
('Tata', 'Indica', 'Vista', 2009, '1.3L Quadrajet', 'Diesel'),
('Tata', 'Safari', 'EX', 2003, '2.0L TC', 'Diesel'),
('Tata', 'Safari', 'Dicor', 2007, '2.2L Dicor', 'Diesel'),
('Tata', 'Safari Storme', 'EX', 2013, '2.2L Varicor', 'Diesel'),
('Tata', 'Nano', 'Std', 2009, '624cc', 'Petrol'),
('Tata', 'Sumo', 'Gold', 2012, '2.2L Dicor', 'Diesel'),

-- MAHINDRA SCORPIO & BOLERO
('Mahindra', 'Scorpio', 'DX', 2003, '2.6L SZ', 'Diesel'),
('Mahindra', 'Scorpio', 'M2DI', 2007, '2.5L M2DI', 'Diesel'),
('Mahindra', 'Scorpio', 'mHawk', 2010, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio', 'S10', 2015, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Bolero', 'SLX', 2005, '2.5L Di', 'Diesel'),
('Mahindra', 'Bolero', 'ZLX', 2013, '1.5L mHawk', 'Diesel'),
('Mahindra', 'XUV500', 'W8', 2012, '2.2L mHawk', 'Diesel'),

-- SKODA / VW
('Skoda', 'Octavia', 'vRS', 2004, '1.8L Turbo', 'Petrol'),
('Skoda', 'Octavia', 'L&K', 2006, '1.9L TDI', 'Diesel'),
('Skoda', 'Laura', 'L&K', 2010, '2.0L TDI', 'Diesel'),
('Volkswagen', 'Polo', 'Comfortline', 2010, '1.2L MPI', 'Petrol'),
('Volkswagen', 'Polo', 'GT TSI', 2014, '1.2L TSI', 'Petrol'),
('Volkswagen', 'Vento', 'Highline', 2011, '1.6L TDI', 'Diesel'),

-- FORD / CHEVROLET / MITSUBISHI
('Ford', 'Ikon', 'Flair', 2003, '1.3L ROCAM', 'Petrol'),
('Ford', 'Figo', 'EXI', 2010, '1.4L TDCi', 'Diesel'),
('Ford', 'Endeavour', 'Thunder', 2009, '3.0L 4x4', 'Diesel'),
('Chevrolet', 'Beat', 'LS', 2010, '1.2L S-TEC', 'Petrol'),
('Chevrolet', 'Beat', 'Diesel', 2011, '1.0L XSDE', 'Diesel'),
('Chevrolet', 'Cruze', 'LTZ', 2010, '2.0L VCDi', 'Diesel'),
('Mitsubishi', 'Lancer', 'GLXi', 2000, '1.5L', 'Petrol'),
('Mitsubishi', 'Pajero', 'SFX', 2008, '2.8L', 'Diesel'),
('Fiat', 'Punto', 'Dynamic', 2009, '1.3L Multijet', 'Diesel'),
('Fiat', 'Linea', 'Emotion', 2009, '1.3L Multijet', 'Diesel');

-- 4. INSERT INTO MAIN TABLE
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT s.make_name, s.model_name, s.trim_name, s.m_year, s.e_type, s.f_type, m.id
FROM (SELECT DISTINCT * FROM legacy_staging) s
JOIN models m ON s.model_name = m.name AND m.make_id = (SELECT id FROM makes WHERE name = s.make_name)
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = s.make_name 
    AND v.model = s.model_name 
    AND v.trim = s.trim_name 
    AND v.manufacture_year = s.m_year
);

DROP TEMPORARY TABLE IF EXISTS legacy_staging;

SET FOREIGN_KEY_CHECKS = 1;
