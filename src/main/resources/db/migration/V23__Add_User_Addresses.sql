-- V23: Implement Multi-Address Support for Users
CREATE TABLE user_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    pincode VARCHAR(10),
    latitude DOUBLE,
    longitude DOUBLE,
    tag VARCHAR(20) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Migration Logic: Seed new table with existing user address data
INSERT INTO user_addresses (user_id, address, city, latitude, longitude, tag, is_default)
SELECT id, address, city, latitude, longitude, 'OTHER', true
FROM users
WHERE address IS NOT NULL AND address <> '';
