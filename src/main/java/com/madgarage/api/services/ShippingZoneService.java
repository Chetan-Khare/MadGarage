package com.madgarage.api.services;

import com.madgarage.api.repository.ShippingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingZoneService {

    private final ShippingZoneRepository shippingZoneRepository;
    private final SystemSettingService systemSettingService;

    /**
     * Resolves the zone ID (1-6) for a given state name.
     * Returns 1 as a fallback if the state name cannot be matched.
     */
    @Cacheable("zones")
    public int getZoneForState(String stateName) {
        if (stateName == null || stateName.trim().isEmpty()) {
            return 1; // Fallback
        }
        return shippingZoneRepository.findByStateNameIgnoreCase(stateName.trim())
                .map(zone -> zone.getZoneId())
                .orElse(1); // Default to Zone 1
    }

    /**
     * Resolves the graduated multiplier based on the distance between the seller and buyer zones.
     * Buyer zone 6 (Northeast) always attracts the NE multiplier override.
     */
    public double getFreightMultiplier(int sellerZone, int buyerZone) {
        if (buyerZone == 6) {
            return systemSettingService.getSettingDouble("FREIGHT_ZONE_MULTIPLIER_NE", 2.25);
        }
        
        int distance = Math.abs(sellerZone - buyerZone);
        String key = "FREIGHT_ZONE_MULTIPLIER_" + Math.min(distance, 4);
        
        // Dynamic DB value with computed safe mathematical fallbacks
        double fallbackValue = 1.0 + (distance * 0.25);
        return systemSettingService.getSettingDouble(key, fallbackValue);
    }
}
