-- V6: Repair Product-Seller links and synchronize category names with frontend filters
-- This script ensures that products are correctly linked to the seller account 
-- even if the user IDs shifted during manual testing/registration.

-- 1. Resolve Seller ID by email to prevent Foreign Key failures if id=2 is missing
SET @seller_id = (SELECT id FROM users WHERE email = 'seller@madgarage.com' LIMIT 1);

-- 2. If seller doesn't exist, create it (fallback for fresh DBs without V2)
/*
INSERT IGNORE INTO users (first_name, last_name, email, password, role, is_active, phone, created_at, updated_at)
SELECT 'Premium', 'Seller', 'seller@madgarage.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'ROLE_SELLER', true, '8888888888', NOW(), NOW()
WHERE @seller_id IS NULL;

-- 3. Update @seller_id again in case we just created it
SET @seller_id = (SELECT id FROM users WHERE email = 'seller@madgarage.com' LIMIT 1);

-- 4. Re-insert products with Simplified Categories (matching HomeScreen.tsx filters)
-- Category mapping: "Brakes & Rotors" -> "Brakes", "Engine Components" -> "Engine"
INSERT IGNORE INTO products (sku, brand, part_name, category, price, description, image_url, color, stock_quantity, seller_id, fitment_category, part_condition) VALUES 
('MG-BRK-001', 'Bosch', 'Premium Ceramic Brake Pads', 'Brakes', 3500.00, 'Low-dust, high-performance ceramic brake pads for smooth stopping.', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500', 'Grey', 50, @seller_id, 'BODY', 'NEW'),
('MG-ENG-002', 'K&N', 'High-Flow Air Filter', 'Engine', 4500.00, 'Washable and reusable air filter designed to increase horsepower.', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=500', 'Red', 30, @seller_id, 'Engine', 'NEW'),
('MG-ENG-003', 'NGK', 'Iridium IX Spark Plugs', 'Engine', 1200.00, 'Set of 4 premium iridium spark plugs for better ignition and fuel efficiency.', 'https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=500', 'Silver', 100, @seller_id, 'ENGINE', 'NEW'),
('MG-LGT-004', 'Ofram', 'Night Breaker LED H7 Bulbs', 'Lighting', 2800.00, 'Ultra-bright white LED bulbs with 200% more light on the road.', 'https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=500', 'White', 45, @seller_id, 'BODY', 'NEW'),
('MG-ENG-005', 'Castrol', 'Magnatec 5W-40 Synthetic Oil (5L)', 'Engine', 1850.00, 'Full synthetic engine oil with intelligent molecules for non-stop protection.', 'https://images.unsplash.com/photo-1597838816882-4551b4631336?w=500', 'Gold', 80, @seller_id, 'ENGINE', 'NEW'),
('MG-SUS-006', 'Monroe', 'Front Strut Assembly', 'Suspension', 5200.00, 'Gas-charged shock absorber specifically tuned for Indian road conditions.', 'https://images.unsplash.com/photo-1609521263047-f8f205293f24?w=500', 'Black', 20, @seller_id, 'BODY', 'NEW'),
('MG-ELE-007', 'Hella', 'Sharptone Horn Set', 'Electrical', 1450.00, 'Twin-tone horn set with clear and loud sound projection.', 'https://images.unsplash.com/photo-1486496146582-9ffcd0b2b2b7?w=500', 'Yellow/Blue', 60, @seller_id, 'BODY', 'NEW'),
('MG-FLT-008', 'Purolator', 'Heavy-Duty Oil Filter', 'Filters', 450.00, 'Premium filtration media that removes up to 99% of engine contaminants.', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=500', 'White', 150, @seller_id, 'ENGINE', 'NEW'),
('MG-ELE-009', 'Exide', 'Mileage Hybrid Battery', 'Electrical', 6800.00, 'Maintenance-free battery with high cranking power for reliable starts.', 'https://images.unsplash.com/photo-1620939511593-353d7f9554bf?w=500', 'Red/Black', 15, @seller_id, 'BODY', 'NEW'),
('MG-INT-010', 'Generic', 'Premium Leatherette Seat Covers', 'Interior', 8500.00, 'Custom-fit water-resistant seat covers for a luxury interior feel.', 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=500', 'Tan Brown', 10, @seller_id, 'INTERIOR', 'NEW');
*/


-- 5. Force update existing products to have simplified categories if they were already inserted
UPDATE products SET category = 'Brakes' WHERE category = 'Brakes & Rotors';
UPDATE products SET category = 'Engine' WHERE category = 'Engine Components';
UPDATE products SET category = 'Filters' WHERE category = 'Filters';
UPDATE products SET category = 'Interior' WHERE category = 'Interior';
UPDATE products SET category = 'Electrical' WHERE category = 'Electrical';
UPDATE products SET category = 'Suspension' WHERE category = 'Suspension';
UPDATE products SET category = 'Lighting' WHERE category = 'Lighting';
