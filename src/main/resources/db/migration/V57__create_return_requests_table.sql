CREATE TABLE return_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    description TEXT,
    image_urls TEXT,
    status VARCHAR(50) NOT NULL,
    replacement_order_id BIGINT,
    admin_note TEXT,
    requested_at DATETIME NOT NULL,
    resolved_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
