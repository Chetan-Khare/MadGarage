package com.madgarage.api.services;

import com.madgarage.api.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
}
