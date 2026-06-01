-- V68: Add partial refund fields and return_request_items table
ALTER TABLE return_requests ADD COLUMN refund_amount DOUBLE PRECISION;
ALTER TABLE return_requests ADD COLUMN refund_id VARCHAR(255);

CREATE TABLE return_request_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    return_request_id BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (return_request_id) REFERENCES return_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE
);
