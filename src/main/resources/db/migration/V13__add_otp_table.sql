-- V13: Persistent OTP storage
CREATE TABLE otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expiry_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_otp_phone (phone)
);
