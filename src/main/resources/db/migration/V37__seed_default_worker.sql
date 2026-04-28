-- V37: Seed default Worker account for testing
-- Password is 'password' (BCrypt hash)
INSERT IGNORE INTO users (first_name, last_name, email, password, role, is_active, phone, created_at, updated_at)
VALUES ('Operational', 'Staff', 'worker@madgarage.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnC', 'ROLE_WORKER', true, '8888888888', NOW(), NOW());
