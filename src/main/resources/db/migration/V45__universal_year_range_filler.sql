-- V45: Universal Year Range Filler
-- This script ensures that for every unique vehicle configuration (Make/Model/Trim/Engine/Fuel),
-- all years between the earliest and latest recorded year are populated.
-- This creates a seamless "Year" selection experience for all cars in the catalog.

SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO vehicles (make, model, trim, manufacture_year, engine_type, fuel_type, model_id)
WITH RECURSIVE year_gen AS (
    -- Generates a list of all possible years from 2000 to 2026
    SELECT 2000 AS y
    UNION ALL
    SELECT y + 1 FROM year_gen WHERE y < 2026
),
vehicle_bounds AS (
    -- Finds the first and last year for every unique vehicle variant currently in the DB
    SELECT 
        make, 
        model, 
        trim, 
        engine_type, 
        fuel_type, 
        model_id,
        MIN(manufacture_year) as start_y,
        MAX(manufacture_year) as end_y
    FROM vehicles
    GROUP BY make, model, trim, engine_type, fuel_type, model_id
    HAVING MIN(manufacture_year) < MAX(manufacture_year) -- Only process variants that have a range
)
SELECT 
    b.make, 
    b.model, 
    b.trim, 
    y.y, 
    b.engine_type, 
    b.fuel_type, 
    b.model_id
FROM vehicle_bounds b
JOIN year_gen y ON y.y >= b.start_y AND y.y <= b.end_y;

SET FOREIGN_KEY_CHECKS = 1;
