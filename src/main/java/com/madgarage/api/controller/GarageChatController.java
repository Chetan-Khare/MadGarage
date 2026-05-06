package com.madgarage.api.controller;

import com.madgarage.api.dto.ChatUserSummaryDto;
import com.madgarage.api.services.GarageAssistantService;
import com.madgarage.api.services.GarageAssistantService.AssistantResult;
import com.madgarage.api.services.RateLimitingService;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import com.madgarage.api.model.ChatMessage;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/assistant")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class GarageChatController {

    private final GarageAssistantService assistantService;
    private final RateLimitingService rateLimitingService;
    private final UserRepository userRepository;

    public GarageChatController(GarageAssistantService assistantService, 
                                RateLimitingService rateLimitingService,
                                UserRepository userRepository) {
        this.assistantService = assistantService;
        this.rateLimitingService = rateLimitingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Mad Garage API is REACHABLE! 🏎️");
    }

    @PostMapping("/chat")
    public ResponseEntity<AssistantResult> chat(
            Principal principal,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        String identifier = principal.getName();
        ConsumptionProbe probe = rateLimitingService.probeAssistant(identifier);
        
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // ── File size guard: reject any image > 5MB ────────────────────────
            List<byte[]> imageBytesList = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile img : images) {
                    if (img != null && !img.isEmpty()) {
                        if (img.getSize() > 5 * 1024 * 1024) {
                            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
                        }
                        imageBytesList.add(img.getBytes());
                    }
                }
            }

            // Images are sent to Gemini transiently and NEVER stored (imageUrl = null).
            // This is intentional — see ChatMessage.imageUrl Javadoc.
            AssistantResult result = assistantService.analyzeCarAndFindParts(user.getId(), message, imageBytesList, null);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Chat interaction failed for {}: {}", identifier, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(Principal principal) {
        String identifier = principal.getName();
        User user = userRepository.findByEmail(identifier)
            .or(() -> userRepository.findByPhone(identifier))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(assistantService.getChatHistory(user.getId()));
    }

    // ── Admin Audit Endpoints ────────────────────────────────────────────────
    
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatUserSummaryDto>> getChattingUsers() {
        // Find users who have at least one chat message and map to DTO to protect PII
        List<ChatUserSummaryDto> dtos = userRepository.findUsersWithChatHistory().stream()
            .map(u -> new ChatUserSummaryDto(u.getId(), u.getFullName(), u.getEmail(), u.getPhone(), u.getRole().toString()))
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/admin/history/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ChatMessage>> getAdminUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        // Use pagination to prevent OOM/timeouts on large conversation logs
        return ResponseEntity.ok(assistantService.getChatHistoryPaged(userId, page, size));
    }
}