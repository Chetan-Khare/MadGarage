package com.madgarage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
            "status", "UP",
            "message", "The Mad Garage API is operational!",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}
