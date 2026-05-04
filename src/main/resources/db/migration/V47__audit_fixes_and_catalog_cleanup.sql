-- V47: Catalog Audit Fixes and Cleanup
-- 1. FIX: 8th Gen Honda Civic Trims (Correcting erroneous 'VX'/'ZX' trims from V41 to 'V')
-- 2. CLEANUP: Consolidate 'Force' and 'Force Motors' to prevent UI duplicates.
-- 3. FIX: Ensure Maruti Suzuki is the unified name for all Maruti entries.

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Correct 8th Gen Civic Trims (2006-2013 used E, S, V; not VX/ZX)
UPDATE vehicles 
SET trim = 'V' 
WHERE make = 'Honda' AND model = 'Civic' AND trim IN ('VX', 'ZX') AND manufacture_year < 2014;

-- 2. Consolidate Force Motors
-- Find the ID of 'Force' and update all models to point to it, then delete 'Force Motors'
SET @force_id = (SELECT id FROM makes WHERE name = 'Force' LIMIT 1);
UPDATE models SET make_id = @force_id WHERE make_id = (SELECT id FROM makes WHERE name = 'Force Motors' LIMIT 1);
UPDATE vehicles SET make = 'Force' WHERE make = 'Force Motors';
DELETE FROM makes WHERE name = 'Force Motors';

-- 3. Unified Maruti Suzuki naming
UPDATE vehicles SET make = 'Maruti Suzuki' WHERE make = 'Maruti';

-- 4. Re-run Universal Filler safety check for Civic
-- Ensure no bridging between old generation (ending 2013) and new generation (starting 2019)
DELETE FROM vehicles 
WHERE model = 'Civic' 
AND manufacture_year BETWEEN 2014 AND 2018;

SET FOREIGN_KEY_CHECKS = 1;
