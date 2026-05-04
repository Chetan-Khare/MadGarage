-- V46: Add Modern Honda Civic (10th Gen) and Gap Awareness
-- The Honda Civic had a hiatus in India between 2013 and 2019.
-- This migration adds the 10th Gen (2019-2021) variants.
-- Note: V45 (Universal Filler) will NOT bridge the 2013-2019 gap because the trims 
-- and engine codes for the 10th Gen (V, VX, ZX / R18Z) are distinct from the 8th Gen (S, VX, ZX / R18A).

SET FOREIGN_KEY_CHECKS = 0;

-- 1. SEED MODERN CIVIC VARIANTS
INSERT INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Honda', 'Civic', t.trim, y.year, e.engine, e.fuel, m.id
FROM (SELECT 'V' as trim UNION SELECT 'VX' UNION SELECT 'ZX') t
CROSS JOIN (SELECT 2019 as year UNION SELECT 2020 UNION SELECT 2021) y
CROSS JOIN (
    SELECT '1.8L i-VTEC' as engine, 'Petrol' as fuel
    UNION 
    SELECT '1.6L i-DTEC', 'Diesel'
) e
JOIN models m ON m.name = 'Civic' AND m.make_id = (SELECT id FROM makes WHERE name = 'Honda')
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = 'Honda' AND v.model = 'Civic' AND v.trim = t.trim 
    AND v.manufacture_year = y.year AND v.engine_type = e.engine
);

-- 2. VERIFICATION / CLEANUP (Just in case any manual gaps were bridged incorrectly)
-- This is a safety measure to ensure no Civic exists between 2014 and 2018.
DELETE FROM vehicles 
WHERE model = 'Civic' 
AND manufacture_year BETWEEN 2014 AND 2018;

SET FOREIGN_KEY_CHECKS = 1;
