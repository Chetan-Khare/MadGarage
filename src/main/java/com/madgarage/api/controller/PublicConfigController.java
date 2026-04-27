package com.madgarage.api.controller;

import com.madgarage.api.model.SystemSetting;
import com.madgarage.api.repository.SystemSettingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cache.annotation.Cacheable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config/public")
public class PublicConfigController {

    private final SystemSettingRepository settingRepository;

    public PublicConfigController(SystemSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @GetMapping
    public Map<String, String> getPublicConfig() {
        Map<String, String> publicMap = new HashMap<>();
        try {
            List<SystemSetting> settings = settingRepository.findAll();
            
            // Filter and return only non-sensitive global settings
            for (SystemSetting setting : settings) {
                String key = setting.getConfigKey();
                if (key != null && (key.equals("SHIPPING_FEE") || key.equals("FREE_SHIPPING_THRESHOLD") || key.equals("PLATFORM_FEE"))) {
                    publicMap.put(key, setting.getConfigValue());
                }
            }
        } catch (Exception e) {
            // Log the error and return safe defaults to prevent app startup failure
            System.err.println("Database error fetching public config: " + e.getMessage());
            publicMap.put("SHIPPING_FEE", "250");
            publicMap.put("FREE_SHIPPING_THRESHOLD", "400");
            publicMap.put("PLATFORM_FEE", "7");
        }
        return publicMap;
    }
}
