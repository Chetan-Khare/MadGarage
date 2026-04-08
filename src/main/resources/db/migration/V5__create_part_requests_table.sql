CREATE TABLE IF NOT EXISTS part_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    make VARCHAR(255),
    model VARCHAR(255),
    year INT,
    part_name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_part_request_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
