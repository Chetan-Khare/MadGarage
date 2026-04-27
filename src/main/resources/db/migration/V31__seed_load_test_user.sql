-- V31: Add a dedicated Load Test user for k6 performance benchmarking
-- Password is 'password' hashed with BCrypt
/*
INSERT IGNORE INTO users (first_name, last_name, email, password, role, is_active, phone, created_at, updated_at)
VALUES ('Load', 'Tester', 'loadtest@madgarage.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'ROLE_CUSTOMER', true, '9999999999', NOW(), NOW());
*/

