package com.madgarage.api.controller;

import com.madgarage.api.dto.PartnerRequestDTO;
import com.madgarage.api.dto.PartnerRequestsSummaryResponse;
import com.madgarage.api.services.PartnerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PartnerRequestController {

    private final PartnerRequestService partnerRequestService;

    /**
     * Public endpoint for new business applications.
     */
    @PostMapping("/api/public/partner-requests")
    public ResponseEntity<String> submitPartnerRequest(@Valid @RequestBody PartnerRequestDTO dto) {
        partnerRequestService.submitRequest(dto);
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
        return ResponseEntity.ok("Request marked as contacted.");
    }

    /**
     * Approve application and create user account.
     */
    @PostMapping("/api/admin/partner-requests/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        String tempPassword = partnerRequestService.approveRequest(id);
        return ResponseEntity.ok("Account created. Temporary password: " + tempPassword);
    }

    /**
     * Decline application.
     */
    @DeleteMapping("/api/admin/partner-requests/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WORKER')")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        partnerRequestService.rejectRequest(id);
        return ResponseEntity.ok("Application rejected.");
    }
}
