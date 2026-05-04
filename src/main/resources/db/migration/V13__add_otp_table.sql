-- V13: Persistent OTP storage (Hardened for hashing)
CREATE TABLE otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    otp_code VARCHAR(255) NOT NULL,
    expiry_time DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_phone (phone),
    INDEX idx_otp_expiry (expiry_time)
);
