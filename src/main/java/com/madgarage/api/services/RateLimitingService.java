package com.madgarage.api.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitingService handles cost-control and security throttling.
 * It prevents SMS spamming (saving money) and brute-force login attempts.
 */
@Service
public class RateLimitingService {

    private final Map<String, Bucket> otpPhoneBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> otpIpBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authFailureBuckets = new ConcurrentHashMap<>();

    /**
     * Dual-Key Rule A: Max 3 OTP requests per 15 minutes per phone number.
     */
    public ConsumptionProbe probeOtpByPhone(String phone) {
        Bucket bucket = otpPhoneBuckets.computeIfAbsent(phone, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(3).refillIntervally(3, Duration.ofMinutes(15)).build())
                .build()
        );
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    /**
     * Dual-Key Rule B: Max 10 OTP requests per hour per IP address.
     */
    public ConsumptionProbe probeOtpByIp(String ip) {
        Bucket bucket = otpIpBuckets.computeIfAbsent(ip, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(10).refillIntervally(10, Duration.ofHours(1)).build())
                .build()
        );
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    /**
     * Brute-Force Key: Max 5 failed login/verify attempts per 15 minutes per identifier.
     */
    public ConsumptionProbe probeAuthAttempt(String identifier) {
        Bucket bucket = authFailureBuckets.computeIfAbsent(identifier, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(5).refillIntervally(5, Duration.ofMinutes(15)).build())
                .build()
        );
        // We probe without consuming (consumption only on failure)
        return bucket.tryConsumeAndReturnRemaining(0);
    }

    public void recordAuthFailure(String identifier) {
        Bucket bucket = authFailureBuckets.get(identifier);
        if (bucket != null) {
            bucket.tryConsume(1);
        }
    }

    public void resetAuthAttempts(String identifier) {
        authFailureBuckets.remove(identifier);
    }
}
