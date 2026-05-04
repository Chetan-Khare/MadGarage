-- V48: Expand Tata Safari Legacy Year Ranges
-- The Safari variants (EX TC and Dicor) were not bridged by the universal filler 
-- because they are technically different engine/trim combinations.
-- This migration manually expands their production years to ensure a complete selection.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. TEMPORARY TABLE FOR SAFARI YEAR EXPANSION
CREATE TEMPORARY TABLE IF NOT EXISTS safari_expansion (
    trim_name VARCHAR(100),
    start_year INT,
    end_year INT,
    e_type VARCHAR(100),
    f_type VARCHAR(50)
);

INSERT INTO safari_expansion VALUES 
-- Tata Safari
('EX', 2000, 2005, '2.0L TC', 'Diesel'),
('Dicor', 2005, 2012, '2.2L Dicor', 'Diesel'),
-- Mahindra Scorpio
('DX', 2003, 2006, '2.6L SZ', 'Diesel'),
('M2DI', 2007, 2012, '2.5L M2DI', 'Diesel'),
('mHawk', 2010, 2014, '2.2L mHawk', 'Diesel'),
-- Tata Indica
('V2', 2000, 2008, '1.4L DL', 'Diesel'),
('Vista', 2008, 2015, '1.3L Quadrajet', 'Diesel');

-- 2. GENERATE AND INSERT ALL YEARS
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
WITH RECURSIVE years AS (
    SELECT 2000 AS year
    UNION ALL
    SELECT year + 1 FROM years WHERE year < 2025
)
SELECT 
    CASE 
        WHEN e.trim_name IN ('DX', 'M2DI', 'mHawk') THEN 'Mahindra'
        ELSE 'Tata'
    END,
    CASE 
        WHEN e.trim_name IN ('DX', 'M2DI', 'mHawk') THEN 'Scorpio'
        WHEN e.trim_name IN ('V2', 'Vista') THEN 'Indica'
        ELSE 'Safari'
    END,
    e.trim_name, 
    y.year, 
    e.e_type, 
    e.f_type,
    m.id
FROM safari_expansion e
JOIN years y ON y.year >= e.start_year AND y.year <= e.end_year
JOIN models m ON m.name = (
    CASE 
        WHEN e.trim_name IN ('DX', 'M2DI', 'mHawk') THEN 'Scorpio'
        WHEN e.trim_name IN ('V2', 'Vista') THEN 'Indica'
        ELSE 'Safari'
    END
) AND m.make_id = (SELECT id FROM makes WHERE name = (
    CASE 
        WHEN e.trim_name IN ('DX', 'M2DI', 'mHawk') THEN 'Mahindra'
        ELSE 'Tata'
    END
));

DROP TEMPORARY TABLE IF EXISTS safari_expansion;
SET FOREIGN_KEY_CHECKS = 1;
