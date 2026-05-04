-- V50: Audit Fixes — Engine Name Unification & Data Corrections
-- Addresses all findings from the V39-V49 migration audit.
-- All operations are idempotent (UPDATE + INSERT IGNORE).

-- =====================================================================
-- FIX #5 (HIGH): Unify Maruti Swift engine name
-- V39 used '1.3L G13', V40 used '1.3L G13BB' — same engine, different names.
-- Standardising to '1.3L G13BB' (the full, correct designation).
-- =====================================================================
UPDATE vehicles 
SET engine_type = '1.3L G13BB' 
WHERE make = 'Maruti Suzuki' AND model = 'Swift' AND engine_type = '1.3L G13';

-- =====================================================================
-- FIX #6 (HIGH): Unify Maruti Alto engine name
-- V39 used '796cc' for LX and '800cc' for LXi — same F8D engine.
-- Standardising to '796cc' (the actual displacement).
-- =====================================================================
UPDATE vehicles 
SET engine_type = '796cc' 
WHERE make = 'Maruti Suzuki' AND model = 'Alto' AND engine_type = '800cc';

-- Also unify Maruti 800 model if it has the same issue
UPDATE vehicles 
SET engine_type = '796cc' 
WHERE make = 'Maruti Suzuki' AND model = '800' AND engine_type = '800cc';

-- =====================================================================
-- FIX #8 (MED): Unify Mahindra Scorpio engine casing
-- V39 used '2.5L M2DI', V40 used '2.5L M2Di' — casing inconsistency.
-- Standardising to '2.5L M2Di' (official Mahindra designation).
-- =====================================================================
UPDATE vehicles 
SET engine_type = '2.5L M2Di' 
WHERE make = 'Mahindra' AND model = 'Scorpio' AND engine_type = '2.5L M2DI';

-- =====================================================================
-- FIX #7 (MED): Correct Ford Endeavour "Thunder" entry
-- "Thunder" was a marketing name, not a trim. '3.0L 4x4' is not a real engine code.
-- Correcting to proper trim 'XLT 4x4' and engine '3.0L TDCi' to match V41 entries.
-- =====================================================================
UPDATE vehicles 
SET trim = 'XLT 4x4', engine_type = '3.0L TDCi' 
WHERE make = 'Ford' AND model = 'Endeavour' AND trim = 'Thunder' AND engine_type = '3.0L 4x4';

-- =====================================================================
-- FIX #13 (MED): Restore Honda Civic 8th Gen distinct trims
-- V47 incorrectly merged VX and ZX into 'V'. The 8th Gen Civic DID have
-- distinct S, V, VX, ZX trims in India. Restoring the original trim names.
-- We can identify rows by year since V41 seeded specific years per trim:
--   S  = 2006, S MT = 2010, VX = 2007, ZX = 2008
-- After V47's UPDATE, 2007 and 2008 rows now say 'V' — fix them back.
-- =====================================================================
UPDATE vehicles 
SET trim = 'VX' 
WHERE make = 'Honda' AND model = 'Civic' AND trim = 'V' 
AND manufacture_year = 2007 AND engine_type = '1.8L R18A';

UPDATE vehicles 
SET trim = 'ZX' 
WHERE make = 'Honda' AND model = 'Civic' AND trim = 'V' 
AND manufacture_year = 2008 AND engine_type = '1.8L R18A';

-- =====================================================================
-- FIX #1 (LOW): Unify Honda Civic engine branding
-- V41 used '1.8L R18A' (internal code). Other Honda entries use the 
-- marketing name 'i-VTEC'. Standardising to '1.8L i-VTEC' for consistency.
-- =====================================================================
UPDATE vehicles 
SET engine_type = '1.8L i-VTEC' 
WHERE make = 'Honda' AND model = 'Civic' AND engine_type = '1.8L R18A';

-- =====================================================================
-- CLEANUP: Remove duplicate rows that may have been created by the
-- engine name splits. After unification, some rows may now violate the
-- unique constraint if we were to re-insert. We keep the lowest ID.
-- =====================================================================
DELETE v1 FROM vehicles v1
JOIN vehicles v2 ON 
    v1.make = v2.make AND 
    v1.model = v2.model AND 
    v1.trim = v2.trim AND 
    v1.manufacture_year = v2.manufacture_year AND 
    v1.fuel_type = v2.fuel_type AND 
    v1.engine_type = v2.engine_type AND 
    v1.id > v2.id;
