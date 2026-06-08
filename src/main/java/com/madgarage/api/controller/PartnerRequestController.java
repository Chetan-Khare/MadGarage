package com.madgarage.api.controller;

import com.madgarage.api.dto.PartnerRequestDTO;
import com.madgarage.api.dto.PartnerRequestsSummaryResponse;
import com.madgarage.api.services.PartnerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PartnerRequestController {

    private final PartnerRequestService partnerRequestService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Public endpoint for new business applications.
     */
    @PostMapping("/api/public/partner-requests")
    public ResponseEntity<String> submitPartnerRequest(@Valid @RequestBody PartnerRequestDTO dto) {
        com.madgarage.api.model.PartnerRequest saved = partnerRequestService.submitRequest(dto);
        // Broadcast to admin dashboard in real-time — use HashMap (Map.of limit is 10 entries)
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("id", saved.getId());
        payload.put("businessName", saved.getBusinessName());
        payload.put("contactName", saved.getContactName());
        payload.put("email", saved.getEmail());
        payload.put("phone", saved.getPhone());
        payload.put("role", saved.getRole());
        payload.put("city", saved.getCity() != null ? saved.getCity() : "");
        payload.put("state", saved.getState() != null ? saved.getState() : "");
        payload.put("address", saved.getAddress() != null ? saved.getAddress() : "");
        payload.put("status", saved.getStatus().name());
        payload.put("createdAt", saved.getCreatedAt().toString());
        messagingTemplate.convertAndSend("/topic/admin/partner-requests", (Object) Map.of(
            "type", "NEW",
            "payload", payload
        ));
        return ResponseEntity.ok("Application received. Our team will contact you shortly.");
    }

    /**
     * Admin/Worker endpoint to list all applications and partnership stats.
     */
    @GetMapping("/api/admin/partner-requests")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<PartnerRequestsSummaryResponse> getAllRequests() {
        return ResponseEntity.ok(partnerRequestService.getRequestsSummary());
    }

    /**
     * Update internal notes for an application.
     */
    @PutMapping("/api/admin/partner-requests/{id}/notes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> updateNotes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        partnerRequestService.updateNotes(id, body.get("notes"));
        return ResponseEntity.ok("Notes updated.");
    }

    /**
     * Mark application as "CONTACTED".
     */
    @PutMapping("/api/admin/partner-requests/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> markAsContacted(@PathVariable Long id) {
        partnerRequestService.markAsContacted(id);
        messagingTemplate.convertAndSend("/topic/admin/partner-requests", (Object) Map.of(
            "type", "STATUS_UPDATE",
            "payload", Map.of("id", id, "status", "CONTACTED")
        ));
        return ResponseEntity.ok("Request marked as contacted.");
    }

    /**
     * Approve application and create user account.
     */
    @PostMapping("/api/admin/partner-requests/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        String tempPassword = partnerRequestService.approveRequest(id);
        messagingTemplate.convertAndSend("/topic/admin/partner-requests", (Object) Map.of(
            "type", "STATUS_UPDATE",
            "payload", Map.of("id", id, "status", "APPROVED")
        ));
        return ResponseEntity.ok("Account created. Temporary password: " + tempPassword);
    }

    /**
     * Decline application.
     */
    @DeleteMapping("/api/admin/partner-requests/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        partnerRequestService.rejectRequest(id);
        messagingTemplate.convertAndSend("/topic/admin/partner-requests", (Object) Map.of(
            "type", "STATUS_UPDATE",
            "payload", Map.of("id", id, "status", "REJECTED")
        ));
        return ResponseEntity.ok("Application rejected.");
    }
}
