-- V12: Prevent duplicate vehicles via a unique constraint
-- This ensures that the same variant (Make, Model, Trim, Year, Engine) cannot be added twice.

-- 1. CLEANUP: In case any duplicates exist, keep only the lowest ID
DELETE v FROM vehicles v
JOIN vehicles v2 ON 
    v.make = v2.make AND 
    v.model = v2.model AND 
    v.trim = v2.trim AND 
    v.manufacture_year = v2.manufacture_year AND 
    COALESCE(v.fuel_type, '') = COALESCE(v2.fuel_type, '') AND
    COALESCE(v.engine_type, '') = COALESCE(v2.engine_type, '')
WHERE v.id > v2.id;

-- 2. REDUCE COLUMN LENGTHS (Fix for "Specified key was too long; max key length is 3072 bytes")
ALTER TABLE vehicles 
    MODIFY make VARCHAR(100), 
    MODIFY model VARCHAR(100), 
    MODIFY trim VARCHAR(100), 
    MODIFY fuel_type VARCHAR(50), 
    MODIFY engine_type VARCHAR(100);

-- 3. ADD UNIQUE CONSTRAINT
ALTER TABLE vehicles 
ADD CONSTRAINT unique_vehicle_variant 
UNIQUE (make, model, trim, manufacture_year, fuel_type, engine_type);
