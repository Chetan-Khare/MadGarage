-- V66: Align system_settings with the new product-tiered shipping model.
-- The old flat 'SHIPPING_FEE' key is retired. Its purpose is now served by
-- 'SHIPPING_FEE_STANDARD' (seeded in V65). We update descriptions to be accurate.

-- 1. Remove the old flat-rate SHIPPING_FEE row (now replaced by SHIPPING_FEE_STANDARD)
DELETE FROM system_settings WHERE config_key = 'SHIPPING_FEE';

-- 2. Update FREE_SHIPPING_THRESHOLD description to clarify it only applies to STANDARD-class items
UPDATE system_settings
SET description = 'Minimum cart subtotal for free shipping. Applies ONLY to STANDARD-class items. FRAGILE, HEAVY_FREIGHT, and CUSTOM_RATE items are always charged freight.'
WHERE config_key = 'FREE_SHIPPING_THRESHOLD';

-- 3. Ensure the STANDARD tier rate description is clear
UPDATE system_settings
SET description = 'Base shipping fee for STANDARD-class parts (lightweight parcels). Used across checkout and admin configuration.'
WHERE config_key = 'SHIPPING_FEE_STANDARD';

-- 4. Ensure the FRAGILE surcharge description is clear
UPDATE system_settings
SET description = 'Delicate wooden crating & handling surcharge applied to FRAGILE-class parts, on top of the STANDARD base fee.'
WHERE config_key = 'SHIPPING_FEE_FRAGILE';

-- 5. Ensure HEAVY_FREIGHT base fee description is clear
UPDATE system_settings
SET description = 'Base pallet surface freight fee for HEAVY_FREIGHT-class parts. An additional per-kg rate and distance multiplier are applied on top.'
WHERE config_key = 'SHIPPING_FEE_FREIGHT_BASE';

-- 6. Ensure per-kg rate description is clear
UPDATE system_settings
SET description = 'Freight rate charged per kilogram for HEAVY_FREIGHT parts. Multiplied by the item weight_kg. A 1.5x surcharge applies for interstate delivery.'
WHERE config_key = 'SHIPPING_FEE_FREIGHT_PER_KG';
