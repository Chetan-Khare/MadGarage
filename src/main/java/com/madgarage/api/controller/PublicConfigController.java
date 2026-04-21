package com.madgarage.api.controller;

import com.madgarage.api.model.SystemSetting;
import com.madgarage.api.repository.SystemSettingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<SystemSetting> settings = settingRepository.findAll();
        Map<String, String> publicMap = new HashMap<>();
        
        // Filter and return only non-sensitive global settings
        for (SystemSetting setting : settings) {
            String key = setting.getConfigKey();
            if (key.equals("SHIPPING_FEE") || key.equals("FREE_SHIPPING_THRESHOLD") || key.equals("PLATFORM_FEE")) {
                publicMap.put(key, setting.getConfigValue());
            }
        }
        return publicMap;
    }
}
