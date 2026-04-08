package com.madgarage.api.controller;

import com.madgarage.api.dto.PartRequestDto;
import com.madgarage.api.model.PartRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.PartRequestRepository;
import com.madgarage.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class PartRequestController {

    private final PartRequestRepository partRequestRepository;
    private final UserService userService;

    public PartRequestController(PartRequestRepository partRequestRepository, UserService userService) {
        this.partRequestRepository = partRequestRepository;
        this.userService = userService;
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
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping
    public ResponseEntity<List<PartRequest>> getMyRequests(Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        List<PartRequest> myRequests = partRequestRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return ResponseEntity.ok(myRequests);
    }
}
