package com.madgarage.api.controller;

import com.madgarage.api.services.GarageAssistantService;
import com.madgarage.api.services.GarageAssistantService.AssistantResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assistant")
public class GarageChatController {

    private final GarageAssistantService assistantService;

    public GarageChatController(GarageAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantResult> chat(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "image", required = false) MultipartFile image) {

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