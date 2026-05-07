-- V56: Create coupons and coupon_usages tables for Blinkit-style discounts
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED
    discount_amount DOUBLE NOT NULL,
    min_order_amount DOUBLE DEFAULT 0.0,
    max_discount_amount DOUBLE, -- Cap for percentage discounts
    usage_limit INT, -- Global limit
    used_count INT DEFAULT 0,
    max_usage_per_user INT DEFAULT 1,
    start_date DATETIME,
    end_date DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE coupon_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);

-- Add coupon fields to orders
ALTER TABLE orders ADD COLUMN applied_coupon_code VARCHAR(50);
ALTER TABLE orders ADD COLUMN discount_amount DOUBLE DEFAULT 0.0;

-- Seed initial test coupons
INSERT INTO coupons (code, description, discount_type, discount_amount, min_order_amount, usage_limit, max_usage_per_user, is_active)
VALUES ('WELCOME100', '₹100 off on your first order', 'FIXED', 100.0, 499.0, 1000, 1, TRUE);

INSERT INTO coupons (code, description, discount_type, discount_amount, min_order_amount, max_discount_amount, usage_limit, max_usage_per_user, is_active)
VALUES ('SAVE10', '10% off up to ₹500', 'PERCENTAGE', 10.0, 999.0, 500.0, 1000, 1, TRUE);
