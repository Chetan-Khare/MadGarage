package com.madgarage.api.controller;

import com.madgarage.api.services.GarageAssistantService;
import com.madgarage.api.services.GarageAssistantService.AssistantResult;
import com.madgarage.api.services.RateLimitingService;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/assistant")
@PreAuthorize("isAuthenticated()")
public class GarageChatController {

    private final GarageAssistantService assistantService;
    private final RateLimitingService rateLimitingService;

    public GarageChatController(GarageAssistantService assistantService, RateLimitingService rateLimitingService) {
        this.assistantService = assistantService;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Mad Garage API is REACHABLE! 🏎️");
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantResult> chat(
            Principal principal,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String identifier = principal.getName();
        ConsumptionProbe probe = rateLimitingService.probeAssistant(identifier);
        
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            byte[] imageBytes = (image != null && !image.isEmpty()) ? image.getBytes() : null;

            // Now returns a result containing message, products, and a flag
            AssistantResult result = assistantService.analyzeCarAndFindParts(message, imageBytes);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}