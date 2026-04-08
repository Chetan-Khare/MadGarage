-- V10: Extend vehicle catalog with more brands and models
-- This adds Kia, Skoda, Volkswagen, Honda, and MG to the baseline catalog.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. SEED NEW MAKES
INSERT IGNORE INTO makes (name) VALUES 
('Kia'), ('Skoda'), ('Volkswagen'), ('Honda'), ('MG');

-- 2. SEED NEW MODELS
INSERT IGNORE INTO models (name, make_id) SELECT 'Seltos', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Sonet', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Carens', id FROM makes WHERE name = 'Kia';

INSERT IGNORE INTO models (name, make_id) SELECT 'Kushaq', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Slavia', id FROM makes WHERE name = 'Skoda';

INSERT IGNORE INTO models (name, make_id) SELECT 'Taigun', id FROM makes WHERE name = 'Volkswagen';
INSERT IGNORE INTO models (name, make_id) SELECT 'Virtus', id FROM makes WHERE name = 'Volkswagen';

INSERT IGNORE INTO models (name, make_id) SELECT 'City', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Elevate', id FROM makes WHERE name = 'Honda';

INSERT IGNORE INTO models (name, make_id) SELECT 'Hector', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'Astor', id FROM makes WHERE name = 'MG';

INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio-N', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Verna', id FROM makes WHERE name = 'Hyundai';

-- 3. SEED NEW VEHICLES

-- KIA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Kia', 'Seltos', 'GTX+ Turbo', 2024, '1.5L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Seltos' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Kia' AND v.model='Seltos' AND v.trim='GTX+ Turbo') LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Kia', 'Sonet', 'X-Line Diesel', 2024, '1.5L CRDi', 'Diesel', m.id 
FROM models m WHERE m.name = 'Sonet' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Kia' AND v.model='Sonet' AND v.trim='X-Line Diesel') LIMIT 1;

-- HYUNDAI / MAHINDRA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Hyundai', 'Verna', 'SX(O) Turbo', 2024, '1.5L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Verna' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Hyundai' AND v.model='Verna' AND v.trim='SX(O) Turbo') LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Mahindra', 'Scorpio-N', 'Z8 L 4WD', 2024, '2.2L mHawk', 'Diesel', m.id 
FROM models m WHERE m.name = 'Scorpio-N' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Mahindra' AND v.model='Scorpio-N' AND v.trim='Z8 L 4WD') LIMIT 1;

-- SKODA / VW
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Skoda', 'Kushaq', 'Monte Carlo', 2024, '1.5L TSI', 'Petrol', m.id 
FROM models m WHERE m.name = 'Kushaq' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Skoda' AND v.model='Kushaq' AND v.trim='Monte Carlo') LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Volkswagen', 'Virtus', 'GT Plus', 2024, '1.5L TSI', 'Petrol', m.id 
FROM models m WHERE m.name = 'Virtus' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Volkswagen' AND v.model='Virtus' AND v.trim='GT Plus') LIMIT 1;

-- HONDA / MG
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Honda', 'Elevate', 'ZX CVT', 2024, '1.5L i-VTEC', 'Petrol', m.id 
FROM models m WHERE m.name = 'Elevate' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Honda' AND v.model='Elevate' AND v.trim='ZX CVT') LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'MG', 'Hector', 'Savvy Pro', 2024, '2.0L Diesel', 'Diesel', m.id 
FROM models m WHERE m.name = 'Hector' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='MG' AND v.model='Hector' AND v.trim='Savvy Pro') LIMIT 1;

SET FOREIGN_KEY_CHECKS = 1;
