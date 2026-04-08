-- V9: Force-recreate vehicle catalog schema and seed data (COMPREHENSIVE IDEMPOTENT VERSION)
-- This ensures the DB matches JPA entities exactly even if Hibernate auto-generation is disabled.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. ENSURE TABLES EXIST (Non-destructive check)
CREATE TABLE IF NOT EXISTS makes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    make_id BIGINT NOT NULL,
    CONSTRAINT fk_models_make FOREIGN KEY (make_id) REFERENCES makes(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(255),
    model VARCHAR(255),
    trim VARCHAR(255),
    manufacture_year INT NOT NULL,
    engine_type VARCHAR(255),
    fuel_type VARCHAR(255),
    generation VARCHAR(255),
    model_id BIGINT,
    CONSTRAINT fk_vehicles_model FOREIGN KEY (model_id) REFERENCES models(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- 2. SEED MAKES (Using IGNORE to avoid duplicate key errors)
INSERT IGNORE INTO makes (name) VALUES 
('Tata'), ('Mahindra'), ('Maruti Suzuki'), ('Hyundai'), ('Kia'), ('Toyota'), ('Skoda'), ('Volkswagen'), ('Honda'), ('MG');

-- 3. SEED MODELS (Linking to Makes)
INSERT IGNORE INTO models (name, make_id) SELECT 'Nexon', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Harrier', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Safari', id FROM makes WHERE name = 'Tata';
INSERT IGNORE INTO models (name, make_id) SELECT 'Punch', id FROM makes WHERE name = 'Tata';

INSERT IGNORE INTO models (name, make_id) SELECT 'XUV700', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Thar', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Thar Roxx', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio-N', id FROM makes WHERE name = 'Mahindra';
INSERT IGNORE INTO models (name, make_id) SELECT 'Scorpio Classic', id FROM makes WHERE name = 'Mahindra';

INSERT IGNORE INTO models (name, make_id) SELECT 'Swift', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Baleno', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Grand Vitara', id FROM makes WHERE name = 'Maruti Suzuki';
INSERT IGNORE INTO models (name, make_id) SELECT 'Brezza', id FROM makes WHERE name = 'Maruti Suzuki';

INSERT IGNORE INTO models (name, make_id) SELECT 'Creta', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'i20', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Venue', id FROM makes WHERE name = 'Hyundai';
INSERT IGNORE INTO models (name, make_id) SELECT 'Verna', id FROM makes WHERE name = 'Hyundai';

INSERT IGNORE INTO models (name, make_id) SELECT 'Seltos', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Sonet', id FROM makes WHERE name = 'Kia';
INSERT IGNORE INTO models (name, make_id) SELECT 'Carens', id FROM makes WHERE name = 'Kia';

INSERT IGNORE INTO models (name, make_id) SELECT 'Innova Hycross', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Fortuner', id FROM makes WHERE name = 'Toyota';
INSERT IGNORE INTO models (name, make_id) SELECT 'Urban Cruiser Hyryder', id FROM makes WHERE name = 'Toyota';

INSERT IGNORE INTO models (name, make_id) SELECT 'Kushaq', id FROM makes WHERE name = 'Skoda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Slavia', id FROM makes WHERE name = 'Skoda';

INSERT IGNORE INTO models (name, make_id) SELECT 'Taigun', id FROM makes WHERE name = 'Volkswagen';
INSERT IGNORE INTO models (name, make_id) SELECT 'Virtus', id FROM makes WHERE name = 'Volkswagen';

INSERT IGNORE INTO models (name, make_id) SELECT 'City', id FROM makes WHERE name = 'Honda';
INSERT IGNORE INTO models (name, make_id) SELECT 'Elevate', id FROM makes WHERE name = 'Honda';

INSERT IGNORE INTO models (name, make_id) SELECT 'Hector', id FROM makes WHERE name = 'MG';
INSERT IGNORE INTO models (name, make_id) SELECT 'Astor', id FROM makes WHERE name = 'MG';

-- 4. SEED VEHICLES (Linking to Models)
-- For vehicles, we check existence by a unique combination to avoid duplicates on re-runs.

-- TATA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Tata', 'Nexon', 'Fearless+ S', 2024, '1.2L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Nexon' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Tata' AND v.model='Nexon' AND v.trim='Fearless+ S' AND v.manufacture_year=2024) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Tata', 'Harrier', 'Fearless+ S', 2024, '2.0L Kryotec', 'Diesel', m.id 
FROM models m WHERE m.name = 'Harrier' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Tata' AND v.model='Harrier' AND v.trim='Fearless+ S' AND v.manufacture_year=2024) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Tata', 'Safari', 'Empowered+ AWD', 2025, '2.0L Kryotec', 'Diesel', m.id 
FROM models m WHERE m.name = 'Safari' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Tata' AND v.model='Safari' AND v.trim='Empowered+ AWD' AND v.manufacture_year=2025) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Tata', 'Punch', 'Creative+ iCNG', 2024, '1.2L Revotron', 'CNG', m.id 
FROM models m WHERE m.name = 'Punch' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Tata' AND v.model='Punch' AND v.trim='Creative+ iCNG' AND v.manufacture_year=2024) LIMIT 1;

-- MAHINDRA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Mahindra', 'XUV700', 'AX7 L AWD', 2023, '2.2L mHawk', 'Diesel', m.id 
FROM models m WHERE m.name = 'XUV700' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Mahindra' AND v.model='XUV700' AND v.trim='AX7 L AWD' AND v.manufacture_year=2023) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Mahindra', 'Thar Roxx', 'AX7 L 4WD', 2025, '2.0L mStallion', 'Petrol', m.id 
FROM models m WHERE m.name = 'Thar Roxx' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Mahindra' AND v.model='Thar Roxx' AND v.trim='AX7 L 4WD' AND v.manufacture_year=2025) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Mahindra', 'Scorpio-N', 'Z8 L 4WD', 2024, '2.2L mHawk', 'Diesel', m.id 
FROM models m WHERE m.name = 'Scorpio-N' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Mahindra' AND v.model='Scorpio-N' AND v.trim='Z8 L 4WD' AND v.manufacture_year=2024) LIMIT 1;

-- KIA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Kia', 'Seltos', 'GTX+ Turbo', 2024, '1.5L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Seltos' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Kia' AND v.model='Seltos' AND v.trim='GTX+ Turbo' AND v.manufacture_year=2024) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Kia', 'Sonet', 'X-Line Diesel', 2024, '1.5L CRDi', 'Diesel', m.id 
FROM models m WHERE m.name = 'Sonet' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Kia' AND v.model='Sonet' AND v.trim='X-Line Diesel' AND v.manufacture_year=2024) LIMIT 1;

-- HYUNDAI
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Hyundai', 'Creta', 'SX(O) DCT', 2025, '1.5L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Creta' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Hyundai' AND v.model='Creta' AND v.trim='SX(O) DCT' AND v.manufacture_year=2025) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Hyundai', 'Verna', 'SX(O) Turbo', 2024, '1.5L Turbo GDi', 'Petrol', m.id 
FROM models m WHERE m.name = 'Verna' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Hyundai' AND v.model='Verna' AND v.trim='SX(O) Turbo' AND v.manufacture_year=2024) LIMIT 1;

-- TOYOTA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Toyota', 'Innova Hycross', 'ZX(O)', 2024, '2.0L Hybrid', 'Petrol Hybrid', m.id 
FROM models m WHERE m.name = 'Innova Hycross' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Toyota' AND v.model='Innova Hycross' AND v.trim='ZX(O)' AND v.manufacture_year=2024) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Toyota', 'Fortuner', 'GR-S 4X4', 2024, '2.8L GD-6', 'Diesel', m.id 
FROM models m WHERE m.name = 'Fortuner' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Toyota' AND v.model='Fortuner' AND v.trim='GR-S 4X4' AND v.manufacture_year=2024) LIMIT 1;

-- SKODA / VW
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Skoda', 'Kushaq', 'Monte Carlo', 2024, '1.5L TSI', 'Petrol', m.id 
FROM models m WHERE m.name = 'Kushaq' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Skoda' AND v.model='Kushaq' AND v.trim='Monte Carlo' AND v.manufacture_year=2024) LIMIT 1;

INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Volkswagen', 'Virtus', 'GT Plus', 2024, '1.5L TSI', 'Petrol', m.id 
FROM models m WHERE m.name = 'Virtus' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Volkswagen' AND v.model='Virtus' AND v.trim='GT Plus' AND v.manufacture_year=2024) LIMIT 1;

-- HONDA
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Honda', 'Elevate', 'ZX CVT', 2024, '1.5L i-VTEC', 'Petrol', m.id 
FROM models m WHERE m.name = 'Elevate' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='Honda' AND v.model='Elevate' AND v.trim='ZX CVT' AND v.manufacture_year=2024) LIMIT 1;

-- MG
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'MG', 'Hector', 'Savvy Pro', 2024, '2.0L Diesel', 'Diesel', m.id 
FROM models m WHERE m.name = 'Hector' AND NOT EXISTS (SELECT 1 FROM vehicles v WHERE v.make='MG' AND v.model='Hector' AND v.trim='Savvy Pro' AND v.manufacture_year=2024) LIMIT 1;

SET FOREIGN_KEY_CHECKS = 1;
