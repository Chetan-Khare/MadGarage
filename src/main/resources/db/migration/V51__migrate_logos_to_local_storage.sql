-- V51: Migrate All Brand Logos to Local Storage
-- This migration updates the makes table to use self-hosted assets, 
-- removing dependency on external domains for all 31 catalog brands.

-- TOP INDIAN & GLOBAL BRANDS
UPDATE makes SET logo_url = '/images/logos/tata.png' WHERE name = 'Tata';
UPDATE makes SET logo_url = '/images/logos/mahindra.png' WHERE name = 'Mahindra';
UPDATE makes SET logo_url = '/images/logos/hyundai.png' WHERE name = 'Hyundai';
UPDATE makes SET logo_url = '/images/logos/toyota.png' WHERE name = 'Toyota';
UPDATE makes SET logo_url = '/images/logos/honda.png' WHERE name = 'Honda';
UPDATE makes SET logo_url = '/images/logos/kia.png' WHERE name = 'Kia';
UPDATE makes SET logo_url = '/images/logos/skoda.png' WHERE name = 'Skoda';
UPDATE makes SET logo_url = '/images/logos/volkswagen.png' WHERE name = 'Volkswagen';
UPDATE makes SET logo_url = '/images/logos/ford.png' WHERE name = 'Ford';
UPDATE makes SET logo_url = '/images/logos/mg.png' WHERE name = 'MG';
UPDATE makes SET logo_url = '/images/logos/renault.png' WHERE name = 'Renault';
UPDATE makes SET logo_url = '/images/logos/nissan.png' WHERE name = 'Nissan';
UPDATE makes SET logo_url = '/images/logos/chevrolet.png' WHERE name = 'Chevrolet';
UPDATE makes SET logo_url = '/images/logos/mitsubishi.png' WHERE name = 'Mitsubishi';
UPDATE makes SET logo_url = '/images/logos/fiat.png' WHERE name = 'Fiat';

-- LUXURY & PREMIUM
UPDATE makes SET logo_url = '/images/logos/mercedes-benz.png' WHERE name = 'Mercedes-Benz';
UPDATE makes SET logo_url = '/images/logos/bmw.png' WHERE name = 'BMW';
UPDATE makes SET logo_url = '/images/logos/audi.png' WHERE name = 'Audi';
UPDATE makes SET logo_url = '/images/logos/volvo.png' WHERE name = 'Volvo';
UPDATE makes SET logo_url = '/images/logos/land-rover.png' WHERE name = 'Land Rover';
UPDATE makes SET logo_url = '/images/logos/lexus.png' WHERE name = 'Lexus';
UPDATE makes SET logo_url = '/images/logos/porsche.png' WHERE name = 'Porsche';
UPDATE makes SET logo_url = '/images/logos/jaguar.png' WHERE name = 'Jaguar';
UPDATE makes SET logo_url = '/images/logos/mini.png' WHERE name = 'Mini';

-- EV, UTILITY & NICHE
UPDATE makes SET logo_url = '/images/logos/byd.png' WHERE name = 'BYD';
UPDATE makes SET logo_url = '/images/logos/isuzu.png' WHERE name = 'Isuzu';
UPDATE makes SET logo_url = '/images/logos/citroen.png' WHERE name = 'Citroen';
UPDATE makes SET logo_url = '/images/logos/jeep.png' WHERE name = 'Jeep';
UPDATE makes SET logo_url = '/images/logos/datsun.png' WHERE name = 'Datsun';

-- MARUTI SUZUKI & FORCE (Fallback to generic if download failed)
-- Note: These can be manually uploaded to the server to complete the set.
UPDATE makes SET logo_url = '/images/logos/maruti.png' WHERE name = 'Maruti Suzuki';
UPDATE makes SET logo_url = '/images/logos/force.png' WHERE name IN ('Force', 'Force Motors');

-- Cleanup: Any remaining HTTP links point to generic placeholder
UPDATE makes SET logo_url = '/images/logos/generic-brand.png' WHERE logo_url LIKE 'http%';
