-- Force update credentials for seed users to resolve 401 login issues
-- password123 (BCrypt hash)
UPDATE users 
SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC',
    role = 'ROLE_ADMIN'
WHERE email = 'admin@madgarage.com';

UPDATE users 
SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC',
    role = 'ROLE_SELLER'
WHERE email = 'seller@madgarage.com';
