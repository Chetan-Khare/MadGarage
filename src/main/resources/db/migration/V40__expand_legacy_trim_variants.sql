-- V40: Full Trim Expansion for Legacy Vehicles (2000-2020)
-- Accuracy-checked against Indian market specs. Extends V39 with complete trim coverage.

SET FOREIGN_KEY_CHECKS = 0;

-- All models already seeded in V39. No new model inserts needed.

CREATE TEMPORARY TABLE IF NOT EXISTS trim_staging (
    make_name VARCHAR(100),
    model_name VARCHAR(100),
    trim_name VARCHAR(100),
    m_year INT,
    e_type VARCHAR(100),
    f_type VARCHAR(50)
);

INSERT INTO trim_staging (make_name, model_name, trim_name, m_year, e_type, f_type) VALUES

-- ================================================================
-- MARUTI SUZUKI SWIFT (2005-2020) — Full trim coverage
-- Engine: 1.3L G13BB (petrol), 1.3L DDiS (diesel), later 1.2L K12
-- ================================================================
('Maruti Suzuki', 'Swift', 'LXi', 2005, '1.3L G13BB', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi', 2005, '1.3L G13BB', 'Petrol'),
('Maruti Suzuki', 'Swift', 'LDi', 2008, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZDi', 2008, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'LXi', 2011, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2011, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi', 2011, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'LDi', 2011, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'VDi', 2011, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZDi', 2011, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'LXi', 2014, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2014, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi', 2014, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+', 2014, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'LDi', 2014, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'VDi', 2014, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZDi', 2014, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZDi+', 2014, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'LXi', 2018, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VXi', 2018, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+', 2018, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'VDi', 2018, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZDi+', 2018, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Swift', 'ZXi', 2020, '1.2L K12', 'Petrol'),
('Maruti Suzuki', 'Swift', 'ZXi+', 2020, '1.2L K12', 'Petrol'),

-- ================================================================
-- MARUTI SUZUKI WAGON R (2000-2020)
-- Engine: 1.1L F10D (early), 1.0L K10B, later 1.2L K12M
-- ================================================================
('Maruti Suzuki', 'WagonR', 'LX', 2000, '1.1L F10D', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'LXi', 2000, '1.1L F10D', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi', 2004, '1.1L F10D', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'AX', 2006, '1.1L F10D', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'LXi', 2013, '1.0L K10B', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi', 2013, '1.0L K10B', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'LXi CNG', 2015, '1.0L K10B', 'CNG'),
('Maruti Suzuki', 'WagonR', 'VXi CNG', 2016, '1.0L K10B', 'CNG'),
('Maruti Suzuki', 'WagonR', 'LXi', 2019, '1.2L K12M', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'VXi', 2019, '1.2L K12M', 'Petrol'),
('Maruti Suzuki', 'WagonR', 'ZXi', 2019, '1.2L K12M', 'Petrol'),

-- ================================================================
-- MARUTI SUZUKI BALENO (2015-2020)
-- Engine: 1.2L K12C DualJet, 1.3L DDiS diesel
-- ================================================================
('Maruti Suzuki', 'Baleno', 'Sigma', 2015, '1.2L K12C', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Delta', 2015, '1.2L K12C', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Zeta', 2015, '1.2L K12C', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Alpha', 2015, '1.2L K12C', 'Petrol'),
('Maruti Suzuki', 'Baleno', 'Delta Diesel', 2016, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Baleno', 'Zeta Diesel', 2016, '1.3L DDiS', 'Diesel'),
('Maruti Suzuki', 'Baleno', 'Alpha CVT', 2018, '1.2L K12C', 'Petrol'),

-- ================================================================
-- HYUNDAI i20 (2008-2020) — Full trim coverage
-- Engine: 1.2L Kappa, 1.4L Gamma, 1.1L CRDi, 1.4L CRDi
-- ================================================================
('Hyundai', 'i20', 'Era', 2008, '1.1L iRDE', 'Petrol'),
('Hyundai', 'i20', 'Sportz', 2008, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Asta', 2008, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Era Diesel', 2010, '1.1L CRDi', 'Diesel'),
('Hyundai', 'i20', 'Magna Diesel', 2010, '1.1L CRDi', 'Diesel'),
('Hyundai', 'i20', 'Sportz Diesel', 2011, '1.4L CRDi', 'Diesel'),
('Hyundai', 'i20', 'Asta Diesel', 2011, '1.4L CRDi', 'Diesel'),
('Hyundai', 'i20', 'Asta (O)', 2014, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Active S', 2015, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Magna', 2018, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Sportz', 2018, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Asta (O)', 2018, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i20', 'Sportz Diesel', 2018, '1.4L CRDi', 'Diesel'),
('Hyundai', 'i20', 'Asta Diesel', 2018, '1.4L CRDi', 'Diesel'),

-- ================================================================
-- HYUNDAI i10 (2007-2019)
-- Engine: 1.1L iRDE, 1.2L Kappa
-- ================================================================
-- i10 Era 2008 already seeded in V39; adding remaining trims only
('Hyundai', 'i10', 'Sportz', 2010, '1.1L iRDE', 'Petrol'),
('Hyundai', 'i10', 'Asta', 2010, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i10', 'Sportz (O)', 2012, '1.2L Kappa', 'Petrol'),
('Hyundai', 'i10', 'Asta (O)', 2013, '1.2L Kappa', 'Petrol'),

-- ================================================================
-- HYUNDAI CRETA (2015-2020)
-- Engine: 1.6L VTVT (petrol), 1.4L CRDI, 1.6L CRDI (diesel)
-- ================================================================
('Hyundai', 'Creta', 'E', 2015, '1.6L VTVT', 'Petrol'),
('Hyundai', 'Creta', 'S', 2016, '1.6L VTVT', 'Petrol'),
('Hyundai', 'Creta', 'S+', 2016, '1.6L VTVT', 'Petrol'),
('Hyundai', 'Creta', 'E Diesel', 2015, '1.4L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'S Diesel', 2016, '1.6L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'SX Diesel', 2017, '1.6L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'SX+ Diesel', 2018, '1.6L CRDi', 'Diesel'),
('Hyundai', 'Creta', 'SX+ Petrol AT', 2019, '1.6L VTVT', 'Petrol'),
('Hyundai', 'Creta', 'S+ AT', 2019, '1.6L VTVT', 'Petrol'),

-- ================================================================
-- HONDA CITY (2000-2020) — Complete generations
-- Type 1 (1997-2003): 1.5L SOHC D15B
-- Type 2 (2003-2008): 1.5L i-DSi
-- Type 3 (2008-2011): 1.5L i-VTEC
-- Gen 5 (2014-2019): 1.5L i-VTEC (petrol), 1.5L i-DTEC (diesel)
-- ================================================================
('Honda', 'City', 'S', 2000, '1.5L SOHC D15B', 'Petrol'),
('Honda', 'City', 'EXi', 2003, '1.5L i-DSi', 'Petrol'),
('Honda', 'City', 'S', 2008, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'S MT', 2014, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'SV', 2014, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'ZX', 2014, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'SV Diesel', 2014, '1.5L i-DTEC', 'Diesel'),
('Honda', 'City', 'ZX Diesel', 2014, '1.5L i-DTEC', 'Diesel'),
('Honda', 'City', 'ZX CVT', 2017, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'ZX CVT', 2019, '1.5L i-VTEC', 'Petrol'),
('Honda', 'City', 'V MT', 2019, '1.5L i-VTEC', 'Petrol'),

-- ================================================================
-- TOYOTA INNOVA (2005-2016) — Complete trim coverage
-- Engine: 2.5L 2KD-FTV D-4D diesel; petrol: 2.7L 2TR-FE
-- ================================================================
('Toyota', 'Innova', 'E', 2005, '2.5L 2KD-FTV', 'Diesel'),
('Toyota', 'Innova', 'G4', 2008, '2.5L 2KD-FTV', 'Diesel'),
('Toyota', 'Innova', 'GX', 2009, '2.5L 2KD-FTV', 'Diesel'),
('Toyota', 'Innova', 'GX (7 Str)', 2012, '2.5L 2KD-FTV', 'Diesel'),
('Toyota', 'Innova', 'VX', 2013, '2.5L 2KD-FTV', 'Diesel'),
('Toyota', 'Innova', 'GX Petrol', 2010, '2.7L 2TR-FE', 'Petrol'),

-- ================================================================
-- TATA INDICA (2002-2014)
-- Engine: 1.4L MPFI (petrol) or 1.4L DI / 1.3L Quadrajet (diesel)
-- ================================================================
('Tata', 'Indica', 'LE', 2002, '1.4L MPFI', 'Petrol'),
('Tata', 'Indica', 'LS', 2002, '1.4L DI', 'Diesel'),
('Tata', 'Indica', 'LSX', 2005, '1.4L DI', 'Diesel'),
('Tata', 'Indica', 'LX', 2005, '1.4L MPFI', 'Petrol'),
('Tata', 'Indica', 'V2 Turbo', 2006, '1.4L DI Turbo', 'Diesel'),
('Tata', 'Indica', 'eV2', 2012, '1.3L Quadrajet', 'Diesel'),

-- ================================================================
-- MAHINDRA SCORPIO (2002-2020) — Complete trim coverage
-- Engine: 2.6L DI (2002-06), 2.5L M2Di (2006-09),
--         2.2L mHawk (2009+)
-- ================================================================
('Mahindra', 'Scorpio', 'LX', 2003, '2.6L SZ', 'Diesel'),
('Mahindra', 'Scorpio', 'SLE', 2004, '2.6L SZ', 'Diesel'),
('Mahindra', 'Scorpio', 'SLX', 2006, '2.5L M2Di', 'Diesel'),
('Mahindra', 'Scorpio', 'VLX', 2008, '2.5L M2Di', 'Diesel'),
('Mahindra', 'Scorpio', 'S6+', 2014, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio', 'S8', 2015, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio', 'S11', 2016, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio', 'S5', 2018, '2.2L mHawk', 'Diesel'),
('Mahindra', 'Scorpio', 'S7 120', 2020, '2.2L mHawk120', 'Diesel'),

-- ================================================================
-- MAHINDRA BOLERO (2000-2020)
-- Engine: 2.5L DI (early), 2.5L CRDe, 1.5L mHawk75 (post 2014)
-- ================================================================
('Mahindra', 'Bolero', 'DX', 2003, '2.5L DI', 'Diesel'),
('Mahindra', 'Bolero', 'LX', 2005, '2.5L DI', 'Diesel'),
('Mahindra', 'Bolero', 'Plus', 2012, '2.5L CRDe', 'Diesel'),
('Mahindra', 'Bolero', 'Power Plus', 2016, '1.5L mHawk75', 'Diesel'),
('Mahindra', 'Bolero', 'B6 (O)', 2020, '1.5L mHawk75', 'Diesel'),

-- ================================================================
-- SKODA OCTAVIA (2002-2013) — Verified India trims
-- Engine: 1.8L Turbo (petrol), 1.9L TDI / 2.0L TDI (diesel)
-- ================================================================
('Skoda', 'Octavia', 'Classic', 2003, '1.6L 8V', 'Petrol'),
('Skoda', 'Octavia', 'Elegance TDI', 2004, '1.9L TDI', 'Diesel'),
('Skoda', 'Octavia', 'Ambiente TDI', 2006, '1.9L TDI', 'Diesel'),
('Skoda', 'Octavia', 'Scout', 2008, '2.0L TDI', 'Diesel'),

-- ================================================================
-- FORD IKON (2001-2011) — Verified India trims
-- Engine: 1.3L ROCAM (petrol), 1.4L TDCi (diesel)
-- ================================================================
('Ford', 'Ikon', 'Zxi', 2003, '1.3L ROCAM', 'Petrol'),
('Ford', 'Ikon', 'EXi', 2005, '1.3L ROCAM', 'Petrol'),
('Ford', 'Ikon', 'NXT Flair', 2008, '1.4L TDCi', 'Diesel'),
('Ford', 'Ikon', 'NXT EXi', 2009, '1.3L ROCAM', 'Petrol'),

-- ================================================================
-- FORD FIGO (2010-2019) — India market
-- Engine: 1.2L Ti-VCT (petrol), 1.4L TDCi (diesel)
-- ================================================================
('Ford', 'Figo', 'LXi', 2010, '1.2L Ti-VCT', 'Petrol'),
('Ford', 'Figo', 'Titanium', 2012, '1.2L Ti-VCT', 'Petrol'),
('Ford', 'Figo', 'LDi', 2010, '1.4L TDCi', 'Diesel'),
('Ford', 'Figo', 'Titanium Diesel', 2013, '1.4L TDCi', 'Diesel'),
('Ford', 'Figo', 'Titanium+', 2015, '1.5L Ti-VCT', 'Petrol'),

-- ================================================================
-- VOLKSWAGEN POLO (2010-2020)
-- Engine: 1.2L MPI (petrol), 1.2L TSI, 1.5L TDI (diesel)
-- ================================================================
-- Polo Comfortline 2010 already in V39; adding remaining trims only
('Volkswagen', 'Polo', 'Highline', 2012, '1.2L MPI', 'Petrol'),
('Volkswagen', 'Polo', 'Highline Diesel', 2012, '1.5L TDI', 'Diesel'),
('Volkswagen', 'Polo', 'Highline+', 2016, '1.0L TSI', 'Petrol'),
('Volkswagen', 'Polo', 'GTI', 2018, '1.8L TSI', 'Petrol');

-- INSERT INTO MAIN TABLE (Idempotent)
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT s.make_name, s.model_name, s.trim_name, s.m_year, s.e_type, s.f_type, m.id
FROM (SELECT DISTINCT * FROM trim_staging) s
JOIN models m ON s.model_name = m.name 
    AND m.make_id = (SELECT id FROM makes WHERE name = s.make_name)
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = s.make_name 
    AND v.model = s.model_name 
    AND v.trim = s.trim_name 
    AND v.manufacture_year = s.m_year
);

DROP TEMPORARY TABLE IF EXISTS trim_staging;

SET FOREIGN_KEY_CHECKS = 1;
