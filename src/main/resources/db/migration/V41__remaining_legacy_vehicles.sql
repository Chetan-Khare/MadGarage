-- V41: Remaining Legacy Vehicles (2000-2020)
-- Covers: Tata Safari Storme/Indigo, Skoda Superb/Rapid/Yeti,
--         Mahindra XUV500, Maruti Ritz/SX4/Omni/Gypsy,
--         Hyundai Getz/Eon/Accent, Honda Civic,
--         Toyota Etios/Corolla Altis, Ford Endeavour,
--         Chevrolet Cruze/Spark, Fiat Punto/Linea

SET FOREIGN_KEY_CHECKS = 0;

-- 1. NEW MODEL REGISTRATIONS
INSERT IGNORE INTO models (name, make_id) SELECT 'Superb', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Rapid', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Yeti', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Civic', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Accord', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Innova Crysta', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Corolla Altis', id FROM makes WHERE name = 'Toyota';

-- 2. STAGING
CREATE TEMPORARY TABLE IF NOT EXISTS v41_staging (
    make_name VARCHAR(100), model_name VARCHAR(100),
    trim_name VARCHAR(100), m_year INT,
    e_type VARCHAR(100), f_type VARCHAR(50)
);

INSERT INTO v41_staging VALUES
-- TATA SAFARI STORME (2012-2019)
-- Engine: 2.2L Varicor 320/400 diesel
('Tata','Safari Storme','LX',2012,'2.2L Varicor 320','Diesel'),
('Tata','Safari Storme','VX',2013,'2.2L Varicor 320','Diesel'),
('Tata','Safari Storme','VX',2015,'2.2L Varicor 400','Diesel'),
('Tata','Safari Storme','EX',2015,'2.2L Varicor 400','Diesel'),
('Tata','Safari Storme','VX 4x4',2016,'2.2L Varicor 400','Diesel'),
('Tata','Safari Storme','Trident',2017,'2.2L Varicor 400','Diesel'),

-- TATA INDIGO (2002-2014)
-- Engine: 1.4L MPFI (petrol), 1.4L DI / 1.3L Quadrajet (diesel)
('Tata','Indigo','LE',2003,'1.4L MPFI','Petrol'),
('Tata','Indigo','LS',2003,'1.4L DI','Diesel'),
('Tata','Indigo','LX',2005,'1.4L DI','Diesel'),
('Tata','Indigo','SX',2007,'1.4L DI','Diesel'),
('Tata','Indigo','GLS',2010,'1.3L Quadrajet','Diesel'),
('Tata','Indigo','Manza Aura',2012,'1.3L Quadrajet','Diesel'),

-- TATA SUMO (2002-2019)
-- Engine: 3.0L CR4 diesel (post 2011)
('Tata','Sumo','Spacio',2005,'2.0L DI','Diesel'),
('Tata','Sumo','Victa',2007,'2.2L Dicor','Diesel'),
('Tata','Sumo','Gold EX',2012,'2.2L Dicor','Diesel'),
('Tata','Sumo','Gold GX',2015,'2.2L Dicor','Diesel'),

-- SKODA SUPERB (2002-2020)
-- Engine: 1.8L Turbo (petrol), 1.9L TDI / 2.0L TDI (diesel)
('Skoda','Superb','Elegance 1.8T',2004,'1.8L Turbo','Petrol'),
('Skoda','Superb','Elegance TDI',2005,'1.9L TDI','Diesel'),
('Skoda','Superb','L&K TDI',2009,'2.0L TDI','Diesel'),
('Skoda','Superb','Ambition TSI',2013,'1.8L TSI','Petrol'),
('Skoda','Superb','Style TDI',2014,'2.0L TDI','Diesel'),
('Skoda','Superb','L&K TSI',2016,'2.0L TSI','Petrol'),
('Skoda','Superb','Sportline TSI',2020,'2.0L TSI','Petrol'),

-- SKODA RAPID (2011-2020)
-- Engine: 1.6L MPI (petrol), 1.5L TDI (diesel)
('Skoda','Rapid','Ambition',2012,'1.6L MPI','Petrol'),
('Skoda','Rapid','Elegance TDI',2013,'1.5L TDI','Diesel'),
('Skoda','Rapid','Ambition TDI',2014,'1.5L TDI','Diesel'),
('Skoda','Rapid','Style TSI',2017,'1.0L TSI','Petrol'),
('Skoda','Rapid','Monte Carlo',2019,'1.0L TSI','Petrol'),
('Skoda','Rapid','Rider',2020,'1.0L TSI','Petrol'),

-- SKODA YETI (2010-2017)
-- Engine: 2.0L TDI diesel
('Skoda','Yeti','Ambition',2010,'2.0L TDI','Diesel'),
('Skoda','Yeti','Style',2012,'2.0L TDI','Diesel'),

-- XUV500 W4/W6 new; W8 2012 already in V39 — skipped here (WHERE NOT EXISTS handles it, but explicit is cleaner)
('Mahindra','XUV500','W4',2012,'2.2L mHawk','Diesel'),
('Mahindra','XUV500','W6',2013,'2.2L mHawk','Diesel'),
-- W8 2014 is a new year, W8 2012 was in V39
('Mahindra','XUV500','W8',2014,'2.2L mHawk','Diesel'),
('Mahindra','XUV500','W10',2015,'2.2L mHawk','Diesel'),
('Mahindra','XUV500','W10 AWD',2016,'2.2L mHawk','Diesel'),
('Mahindra','XUV500','W7',2018,'2.2L mHawk','Diesel'),
('Mahindra','XUV500','W11',2019,'2.2L mHawk','Diesel'),

-- MARUTI RITZ (2009-2017)
-- Engine: 1.2L K12 (petrol), 1.3L DDiS (diesel)
('Maruti Suzuki','Ritz','LXi',2009,'1.2L K12','Petrol'),
('Maruti Suzuki','Ritz','VXi',2010,'1.2L K12','Petrol'),
('Maruti Suzuki','Ritz','ZXi',2011,'1.2L K12','Petrol'),
('Maruti Suzuki','Ritz','LDi',2009,'1.3L DDiS','Diesel'),
('Maruti Suzuki','Ritz','VDi',2012,'1.3L DDiS','Diesel'),

-- MARUTI SX4 (2007-2014)
-- Engine: 1.6L VVT (petrol), 1.3L DDiS (diesel)
('Maruti Suzuki','SX4','Vxi',2007,'1.6L VVT','Petrol'),
('Maruti Suzuki','SX4','Zxi',2008,'1.6L VVT','Petrol'),
('Maruti Suzuki','SX4','S-Series',2009,'1.3L DDiS','Diesel'),
('Maruti Suzuki','SX4','Green',2012,'1.3L DDiS','Diesel'),

-- MARUTI OMNI (2001-2019)
-- Engine: 796cc F8D petrol
('Maruti Suzuki','Omni','Std',2002,'796cc F8D','Petrol'),
('Maruti Suzuki','Omni','MPI',2006,'796cc F8D','Petrol'),
('Maruti Suzuki','Omni','Cargo',2010,'796cc F8D','Petrol'),
('Maruti Suzuki','Omni','E',2014,'796cc F8D','Petrol'),

-- MARUTI GYPSY (2000-2019)
-- Engine: 1.3L G13T petrol
('Maruti Suzuki','Gypsy','MG410W',2000,'1.3L G13T','Petrol'),
('Maruti Suzuki','Gypsy','Hard Top',2005,'1.3L G13T','Petrol'),
('Maruti Suzuki','Gypsy','Soft Top',2009,'1.3L G13T','Petrol'),

-- HYUNDAI GETZ (2004-2010)
-- Engine: 1.1L iRDE (petrol), 1.5L CRDi (diesel)
('Hyundai','Getz','GS',2004,'1.1L iRDE','Petrol'),
('Hyundai','Getz','GVS',2005,'1.3L iRDE','Petrol'),
('Hyundai','Getz','Prime GS',2007,'1.1L iRDE','Petrol'),
('Hyundai','Getz','Prime GVS CRDi',2008,'1.5L CRDi','Diesel'),

-- HYUNDAI EON (2011-2018)
-- Engine: 0.8L Epsilon
('Hyundai','Eon','Era+',2012,'0.8L Epsilon','Petrol'),
('Hyundai','Eon','Magna+',2013,'0.8L Epsilon','Petrol'),
('Hyundai','Eon','Sportz',2015,'1.0L Epsilon','Petrol'),
('Hyundai','Eon','Sportz+',2016,'1.0L Epsilon','Petrol'),

-- HYUNDAI ACCENT (2000-2014)
-- Engine: 1.5L G4EC (petrol), 1.5L CRDi (diesel)
('Hyundai','Accent','GLE',2000,'1.5L G4EC','Petrol'),
('Hyundai','Accent','GLS',2003,'1.5L G4EC','Petrol'),
('Hyundai','Accent','CRDi',2005,'1.5L CRDi','Diesel'),
('Hyundai','Accent','Viva',2010,'1.4L G4FA','Petrol'),

-- HONDA CIVIC (2006-2013)
-- Engine: 1.8L i-VTEC (petrol)
('Honda','Civic','S',2006,'1.8L R18A','Petrol'),
('Honda','Civic','VX',2007,'1.8L R18A','Petrol'),
('Honda','Civic','ZX',2008,'1.8L R18A','Petrol'),
('Honda','Civic','S MT',2010,'1.8L R18A','Petrol'),

-- HONDA ACCORD (2003-2013)
-- Engine: 2.4L i-VTEC (petrol), 2.2L i-DTEC (diesel)
('Honda','Accord','2.0 MT',2003,'2.0L i-VTEC','Petrol'),
('Honda','Accord','VTi-L',2006,'2.4L i-VTEC','Petrol'),
('Honda','Accord','VTi-L AT',2008,'2.4L i-VTEC','Petrol'),
('Honda','Accord','2.2 i-DTEC',2009,'2.2L i-DTEC','Diesel'),
('Honda','Accord','3.5 V6',2011,'3.5L V6 VTEC','Petrol'),

-- TOYOTA ETIOS (2010-2020)
-- Engine: 1.5L 1NR-FE (petrol), 1.4L D-4D (diesel)
('Toyota','Etios','J',2010,'1.5L 1NR-FE','Petrol'),
('Toyota','Etios','G',2011,'1.5L 1NR-FE','Petrol'),
('Toyota','Etios','V',2012,'1.5L 1NR-FE','Petrol'),
('Toyota','Etios','GD',2012,'1.4L D-4D','Diesel'),
('Toyota','Etios','VD',2013,'1.4L D-4D','Diesel'),
('Toyota','Etios','Cross V',2014,'1.5L 1NR-FE','Petrol'),

-- TOYOTA COROLLA ALTIS (2008-2018)
-- Engine: 1.8L Dual VVT-i (petrol), 1.4L D-4D (diesel)
('Toyota','Corolla Altis','J',2009,'1.8L Dual VVT-i','Petrol'),
('Toyota','Corolla Altis','G',2010,'1.8L Dual VVT-i','Petrol'),
('Toyota','Corolla Altis','VL AT',2013,'1.8L Dual VVT-i','Petrol'),
('Toyota','Corolla Altis','D-4D G',2012,'1.4L D-4D','Diesel'),
('Toyota','Corolla Altis','1.8G CVT',2015,'1.8L Dual VVT-i','Petrol'),

-- FORD ENDEAVOUR (2003-2020) — All generations
-- Engine: 2.5L/3.0L TDCI diesel
('Ford','Endeavour','XLT',2004,'2.5L TDCi','Diesel'),
('Ford','Endeavour','XLT 4x4',2006,'2.5L TDCi','Diesel'),
('Ford','Endeavour','XLT 3.0 4x4',2009,'3.0L TDCi','Diesel'),
('Ford','Endeavour','Limited 3.0',2012,'3.0L TDCi','Diesel'),
('Ford','Endeavour','Titanium 2.2',2016,'2.2L TDCi','Diesel'),
('Ford','Endeavour','Titanium 3.2 4x4',2016,'3.2L TDCi','Diesel'),
('Ford','Endeavour','Titanium+ 2.0',2020,'2.0L EcoBlue','Diesel'),

-- CHEVROLET CRUZE (2009-2017)
-- Engine: 2.0L VCDi diesel
('Chevrolet','Cruze','LS',2009,'2.0L VCDi','Diesel'),
('Chevrolet','Cruze','LT',2010,'2.0L VCDi','Diesel'),
('Chevrolet','Cruze','LTZ',2012,'2.0L VCDi','Diesel'),
('Chevrolet','Cruze','LTZ AT',2014,'2.0L VCDi','Diesel'),

-- CHEVROLET SPARK (2007-2015)
-- Engine: 1.0L XSDE (petrol)
('Chevrolet','Spark','LS',2007,'1.0L B10D1','Petrol'),
('Chevrolet','Spark','LT',2010,'1.0L B10D1','Petrol'),
('Chevrolet','Spark','PS',2012,'1.0L B10D1','Petrol'),

-- FIAT PUNTO (2009-2018)
-- Engine: 1.2L FIRE (petrol), 1.3L Multijet (diesel)
('Fiat','Punto','Emotion',2009,'1.2L FIRE','Petrol'),
('Fiat','Punto','Emotion 1.3',2010,'1.3L Multijet','Diesel'),
('Fiat','Punto','Active',2012,'1.2L FIRE','Petrol'),
('Fiat','Punto','Abarth 1.4T',2015,'1.4L T-Jet','Petrol'),

-- FIAT LINEA (2009-2017)
-- Engine: 1.4L T-Jet (petrol), 1.3L Multijet (diesel)
('Fiat','Linea','Active',2009,'1.4L FIRE','Petrol'),
('Fiat','Linea','Dynamic',2010,'1.4L FIRE','Petrol'),
('Fiat','Linea','T-Jet',2011,'1.4L T-Jet','Petrol'),
('Fiat','Linea','Diesel Active',2010,'1.3L Multijet','Diesel'),
('Fiat','Linea','Diesel Emotion',2012,'1.3L Multijet','Diesel');

-- INSERT (Idempotent)
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT s.make_name, s.model_name, s.trim_name, s.m_year, s.e_type, s.f_type, m.id
FROM (SELECT DISTINCT * FROM v41_staging) s
JOIN models m ON s.model_name = m.name
    AND m.make_id = (SELECT id FROM makes WHERE name = s.make_name)
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v
    WHERE v.make = s.make_name AND v.model = s.model_name
    AND v.trim = s.trim_name AND v.manufacture_year = s.m_year
);

DROP TEMPORARY TABLE IF EXISTS v41_staging;
SET FOREIGN_KEY_CHECKS = 1;
