package com.madgarage.api.controller;

import com.madgarage.api.enums.PartRequestStatus;
import com.madgarage.api.model.PartRequest;
import com.madgarage.api.repository.PartRequestRepository;
import com.madgarage.api.services.ExpoNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/requests")
@PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
public class AdminPartRequestController {

    private final PartRequestRepository partRequestRepository;
    private final ExpoNotificationService notificationService;

    public AdminPartRequestController(PartRequestRepository partRequestRepository,
                                      ExpoNotificationService notificationService) {
        this.partRequestRepository = partRequestRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getAllRequests() {
        List<Map<String, Object>> requests = partRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(req -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", req.getId());
                    map.put("make", req.getMake());
                    map.put("model", req.getModel());
                    map.put("year", req.getYear());
                    map.put("partName", req.getPartName());
                    map.put("description", req.getDescription());
                    map.put("status", req.getStatus().name());
                    map.put("createdAt", req.getCreatedAt());

                    if (req.getUser() != null) {
                        map.put("customerName", req.getUser().getFirstName() + " " + req.getUser().getLastName());
                        map.put("customerPhone", req.getUser().getPhone());
                        map.put("customerEmail", req.getUser().getEmail());
                    } else {
                        map.put("customerName", req.getCustomerName());
                        map.put("customerPhone", req.getCustomerPhone());
                        map.put("customerEmail", "Guest User");
                    }
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        PartRequest request = partRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        try {
            PartRequestStatus newStatus = PartRequestStatus.valueOf(status.toUpperCase());
            request.setStatus(newStatus);
            PartRequest saved = partRequestRepository.save(request);

            // ── Fire-and-forget push notification ────────────────────────────────
            if (saved.getUser() != null && saved.getUser().getExpoPushToken() != null) {
                notificationService.sendStatusUpdateNotification(
                        saved.getUser().getExpoPushToken(),
                        saved.getPartName(),
                        newStatus.name()
                );
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", saved.getId());
            map.put("status", saved.getStatus().name());

            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
    }
}

