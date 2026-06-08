package com.madgarage.api.controller;

import com.madgarage.api.dto.PartRequestDto;
import com.madgarage.api.model.PartRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.PartRequestRepository;
import com.madgarage.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class PartRequestController {

    private final PartRequestRepository partRequestRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public PartRequestController(PartRequestRepository partRequestRepository, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.partRequestRepository = partRequestRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<PartRequest> createPartRequest(Principal principal, @RequestBody PartRequestDto requestDto) {
        User currentUser = null;
        if (principal != null) {
            currentUser = userService.getCurrentUser(principal.getName());
        }
        
        PartRequest partRequest = PartRequest.builder()
                .user(currentUser)
                .make(requestDto.getMake())
                .model(requestDto.getModel())
                .year(requestDto.getYear())
                .partName(requestDto.getPartName())
                .description(requestDto.getDescription())
                .customerName(requestDto.getCustomerName())
                .customerPhone(requestDto.getCustomerPhone())
                .build();
                
        PartRequest saved = partRequestRepository.save(partRequest);
        
        Map<String, Object> map = new HashMap<>();
        map.put("id", saved.getId());
        map.put("make", saved.getMake());
        map.put("model", saved.getModel());
        map.put("year", saved.getYear());
        map.put("partName", saved.getPartName());
        map.put("description", saved.getDescription());
        map.put("status", saved.getStatus().name());
        map.put("createdAt", saved.getCreatedAt());

        if (saved.getUser() != null) {
            map.put("customerName", saved.getUser().getFirstName() + " " + saved.getUser().getLastName());
            map.put("customerPhone", saved.getUser().getPhone());
            map.put("customerEmail", saved.getUser().getEmail());
        } else {
            map.put("customerName", saved.getCustomerName());
            map.put("customerPhone", saved.getCustomerPhone());
            map.put("customerEmail", "Guest User");
        }

        messagingTemplate.convertAndSend("/topic/admin/part-requests", (Object) Map.of(
            "type", "NEW",
            "payload", map
        ));
        
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PartRequest>> getMyRequests(Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        List<PartRequest> myRequests = partRequestRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return ResponseEntity.ok(myRequests);
    }
}
