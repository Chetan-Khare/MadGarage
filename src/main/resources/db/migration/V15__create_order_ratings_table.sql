-- V15__create_order_ratings_table.sql (Renamed from V7 to avoid Flyway out-of-order errors)
CREATE TABLE IF NOT EXISTS `order_ratings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `part_rating` INT NOT NULL,
    `delivery_rating` INT NOT NULL,
    `comment` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_rating_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
);
