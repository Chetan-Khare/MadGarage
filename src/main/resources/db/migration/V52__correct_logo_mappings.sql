-- V52: Correct Brand Logo Mappings
-- Fixes the mappings that were incorrectly set to generic-brand.png in V51.

UPDATE makes SET logo_url = '/images/logos/kia.png' WHERE name = 'Kia';
UPDATE makes SET logo_url = '/images/logos/skoda.png' WHERE name = 'Skoda';
UPDATE makes SET logo_url = '/images/logos/volkswagen.png' WHERE name = 'Volkswagen';
UPDATE makes SET logo_url = '/images/logos/mg.png' WHERE name = 'MG';
UPDATE makes SET logo_url = '/images/logos/chevrolet.png' WHERE name = 'Chevrolet';
UPDATE makes SET logo_url = '/images/logos/mitsubishi.png' WHERE name = 'Mitsubishi';
UPDATE makes SET logo_url = '/images/logos/fiat.png' WHERE name = 'Fiat';
UPDATE makes SET logo_url = '/images/logos/mercedes-benz.png' WHERE name = 'Mercedes-Benz';
UPDATE makes SET logo_url = '/images/logos/bmw.png' WHERE name = 'BMW';
UPDATE makes SET logo_url = '/images/logos/audi.png' WHERE name = 'Audi';
UPDATE makes SET logo_url = '/images/logos/volvo.png' WHERE name = 'Volvo';
UPDATE makes SET logo_url = '/images/logos/land-rover.png' WHERE name = 'Land Rover';
UPDATE makes SET logo_url = '/images/logos/lexus.png' WHERE name = 'Lexus';
UPDATE makes SET logo_url = '/images/logos/porsche.png' WHERE name = 'Porsche';
UPDATE makes SET logo_url = '/images/logos/jaguar.png' WHERE name = 'Jaguar';
UPDATE makes SET logo_url = '/images/logos/mini.png' WHERE name = 'Mini';
UPDATE makes SET logo_url = '/images/logos/byd.png' WHERE name = 'BYD';
UPDATE makes SET logo_url = '/images/logos/isuzu.png' WHERE name = 'Isuzu';
UPDATE makes SET logo_url = '/images/logos/citroen.png' WHERE name = 'Citroen';
UPDATE makes SET logo_url = '/images/logos/jeep.png' WHERE name = 'Jeep';
UPDATE makes SET logo_url = '/images/logos/datsun.png' WHERE name = 'Datsun';
