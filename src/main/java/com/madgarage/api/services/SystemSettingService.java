package com.madgarage.api.services;

import com.madgarage.api.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Cacheable("settings")
    public double getSettingDouble(String key, double fallback) {
        return systemSettingRepository.findByConfigKey(key)
                .map(s -> {
                    try { return Double.parseDouble(s.getConfigValue()); }
                    catch (NumberFormatException e) { return fallback; }
                })
                .orElse(fallback);
    }

    @Cacheable("settings")
    public String getSettingString(String key, String fallback) {
        return systemSettingRepository.findByConfigKey(key)
                .map(s -> s.getConfigValue())
                .orElse(fallback);
    }

    /**
     * Evicts ALL cached settings entries so that updated values are immediately
     * reflected in order-time shipping calculations and the public config endpoint.
     * Called by AdminController whenever a setting is saved.
     */
    @Caching(evict = {
        @CacheEvict(cacheNames = "settings", allEntries = true),
        @CacheEvict(cacheNames = "publicConfig", allEntries = true)
    })
    public void evictSettingsCache() {
        // Intentionally empty — Spring AOP handles the cache eviction
    }
}
