package com.madgarage.api.controller;

import com.madgarage.api.model.SystemSetting;
import com.madgarage.api.repository.SystemSettingRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/config/public")
public class PublicConfigController {

    private final SystemSettingRepository settingRepository;

    // All keys that are safe to expose publicly (no sensitive business data)
    private static final Set<String> PUBLIC_KEYS = Set.of(
        "SHIPPING_FEE",               // Legacy key (backward compat)
        "SHIPPING_FEE_STANDARD",      // STANDARD tier base fee
        "SHIPPING_FEE_FRAGILE",       // FRAGILE surcharge
        "SHIPPING_FEE_FREIGHT_BASE",  // HEAVY_FREIGHT base pallet fee
        "SHIPPING_FEE_FREIGHT_PER_KG",// HEAVY_FREIGHT per-kg rate
        "FREE_SHIPPING_THRESHOLD",    // Minimum cart for free shipping (STANDARD only)
        "PLATFORM_FEE",               // Platform service charge
        "FREIGHT_ZONE_MULTIPLIER_0",
        "FREIGHT_ZONE_MULTIPLIER_1",
        "FREIGHT_ZONE_MULTIPLIER_2",
        "FREIGHT_ZONE_MULTIPLIER_3",
        "FREIGHT_ZONE_MULTIPLIER_4",
        "FREIGHT_ZONE_MULTIPLIER_NE"
    );

    public PublicConfigController(SystemSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @GetMapping
    @Cacheable("publicConfig")
    public Map<String, String> getPublicConfig() {
        Map<String, String> publicMap = new HashMap<>();
        try {
            List<SystemSetting> settings = settingRepository.findAll();
            for (SystemSetting setting : settings) {
                if (setting.getConfigKey() != null && PUBLIC_KEYS.contains(setting.getConfigKey())) {
                    publicMap.put(setting.getConfigKey(), setting.getConfigValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Database error fetching public config: " + e.getMessage());
            // Safe defaults so checkout is never broken if DB is temporarily unreachable
            publicMap.put("SHIPPING_FEE_STANDARD", "150");
            publicMap.put("SHIPPING_FEE_FRAGILE", "1200");
            publicMap.put("SHIPPING_FEE_FREIGHT_BASE", "2000");
            publicMap.put("SHIPPING_FEE_FREIGHT_PER_KG", "15");
            publicMap.put("FREE_SHIPPING_THRESHOLD", "400");
            publicMap.put("PLATFORM_FEE", "7");
        }
        return publicMap;
    }
}
