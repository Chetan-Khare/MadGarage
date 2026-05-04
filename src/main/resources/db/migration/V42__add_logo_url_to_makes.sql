-- V42: Add logo_url to makes table
ALTER TABLE makes ADD COLUMN logo_url VARCHAR(512);

-- Populate with some default logos (Placeholder logos for popular Indian brands)
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/tata-logo.png' WHERE name = 'Tata';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/mahindra-logo.png' WHERE name = 'Mahindra';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/maruti-suzuki-logo.png' WHERE name = 'Maruti Suzuki';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/hyundai-logo.png' WHERE name = 'Hyundai';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/toyota-logo.png' WHERE name = 'Toyota';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/honda-logo.png' WHERE name = 'Honda';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/kia-logo.png' WHERE name = 'Kia';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/skoda-logo.png' WHERE name = 'Skoda';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/volkswagen-logo.png' WHERE name = 'Volkswagen';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/ford-logo.png' WHERE name = 'Ford';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/mg-logo.png' WHERE name = 'MG';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/renault-logo.png' WHERE name = 'Renault';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/nissan-logo.png' WHERE name = 'Nissan';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/chevrolet-logo.png' WHERE name = 'Chevrolet';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/mitsubishi-logo.png' WHERE name = 'Mitsubishi';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/fiat-logo.png' WHERE name = 'Fiat';

-- LUXURY & PREMIUM
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/mercedes-benz-logo.png' WHERE name = 'Mercedes-Benz';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/bmw-logo.png' WHERE name = 'BMW';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/audi-logo.png' WHERE name = 'Audi';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/volvo-logo.png' WHERE name = 'Volvo';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/land-rover-logo.png' WHERE name = 'Land Rover';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/lexus-logo.png' WHERE name = 'Lexus';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/porsche-logo.png' WHERE name = 'Porsche';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/jaguar-logo.png' WHERE name = 'Jaguar';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/mini-logo.png' WHERE name = 'Mini';

-- EV, UTILITY & NICHE
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/byd-logo.png' WHERE name = 'BYD';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/isuzu-logo.png' WHERE name = 'Isuzu';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/citroen-logo.png' WHERE name = 'Citroen';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/jeep-logo.png' WHERE name = 'Jeep';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/datsun-logo.png' WHERE name = 'Datsun';

-- FORCE ALIASES (Covering both common names)
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/force-motors-logo.png' WHERE name = 'Force';
UPDATE makes SET logo_url = 'https://www.carlogos.org/car-logos/force-motors-logo.png' WHERE name = 'Force Motors';
