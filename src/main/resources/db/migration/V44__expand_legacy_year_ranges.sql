-- V44: Expand Legacy Vehicle Year Ranges
-- Filling gaps for popular models from 2000-2010 to ensure a smoother year selection experience.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. TEMPORARY TABLE FOR YEAR GENERATION
CREATE TEMPORARY TABLE IF NOT EXISTS legacy_year_expansion (
    make_name VARCHAR(100),
    model_name VARCHAR(100),
    trim_name VARCHAR(100),
    start_year INT,
    end_year INT,
    e_type VARCHAR(100),
    f_type VARCHAR(50)
);

INSERT INTO legacy_year_expansion VALUES 
-- Maruti 800
('Maruti Suzuki', '800', 'Std', 2000, 2014, '796cc', 'Petrol'),
('Maruti Suzuki', '800', 'DX', 2000, 2014, '796cc', 'Petrol'),
-- Maruti Zen
('Maruti Suzuki', 'Zen', 'LXi', 2000, 2006, '1.0L G10', 'Petrol'),
('Maruti Suzuki', 'Zen', 'VXi', 2000, 2006, '1.0L G10', 'Petrol'),
-- Maruti Esteem
('Maruti Suzuki', 'Esteem', 'LXi', 2000, 2008, '1.3L G13', 'Petrol'),
('Maruti Suzuki', 'Esteem', 'VXi', 2000, 2008, '1.3L G13', 'Petrol'),
-- Maruti Alto
('Maruti Suzuki', 'Alto', 'LX', 2000, 2012, '796cc', 'Petrol'),
('Maruti Suzuki', 'Alto', 'LXi', 2000, 2012, '800cc', 'Petrol'),
-- Hyundai Santro
('Hyundai', 'Santro', 'LP', 2000, 2003, '1.0L Epsilon', 'Petrol'),
('Hyundai', 'Santro Xing', 'XP', 2004, 2014, '1.1L Epsilon', 'Petrol'),
('Hyundai', 'Santro Xing', 'GLS', 2004, 2014, '1.1L Epsilon', 'Petrol'),
-- Tata Indica
('Tata', 'Indica', 'V2', 2000, 2010, '1.4L DL', 'Diesel'),
-- Toyota Qualis
('Toyota', 'Qualis', 'FS', 2000, 2005, '2.4L Diesel', 'Diesel'),
('Toyota', 'Qualis', 'GST', 2000, 2005, '2.4L Diesel', 'Diesel');

-- 2. GENERATE AND INSERT ALL YEARS IN RANGE
-- We use a recursive CTE to generate the integers (years) since we are on MySQL 8.0+
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
WITH RECURSIVE years AS (
    SELECT 2000 AS year
    UNION ALL
    SELECT year + 1 FROM years WHERE year < 2015
)
SELECT 
    e.make_name, 
    e.model_name, 
    e.trim_name, 
    y.year, 
    e.e_type, 
    e.f_type,
    m.id
FROM legacy_year_expansion e
JOIN years y ON y.year >= e.start_year AND y.year <= e.end_year
JOIN models m ON e.model_name = m.name AND m.make_id = (SELECT id FROM makes WHERE name = e.make_name);

DROP TEMPORARY TABLE IF EXISTS legacy_year_expansion;
SET FOREIGN_KEY_CHECKS = 1;
