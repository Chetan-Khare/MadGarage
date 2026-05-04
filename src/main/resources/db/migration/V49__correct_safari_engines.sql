-- V49: Correct Tata Safari Engine Information
-- The original Safari used a 1.9L Turbo (1948cc) engine, not 2.0L.
-- It also featured a 3.0L DiCOR engine between 2005 and 2007 which was missing.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Correct existing 2.0L entries to 1.9L Turbo
UPDATE vehicles 
SET engine_type = '1.9L Turbo' 
WHERE make = 'Tata' AND model = 'Safari' AND engine_type = '2.0L TC';

-- 2. Add the missing 3.0L DiCOR Safari variants (2005-2007)
INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
SELECT 'Tata', 'Safari', t.trim, y.year, '3.0L DiCOR', 'Diesel', m.id
FROM (SELECT 'EX' as trim UNION SELECT 'LX') t
CROSS JOIN (SELECT 2005 as year UNION SELECT 2006 UNION SELECT 2007) y
JOIN models m ON m.name = 'Safari' AND m.make_id = (SELECT id FROM makes WHERE name = 'Tata')
WHERE NOT EXISTS (
    SELECT 1 FROM vehicles v 
    WHERE v.make = 'Tata' AND v.model = 'Safari' AND v.trim = t.trim 
    AND v.manufacture_year = y.year AND v.engine_type = '3.0L DiCOR'
);

SET FOREIGN_KEY_CHECKS = 1;
