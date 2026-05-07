package com.madgarage.api.controller;

import com.madgarage.api.dto.ReturnRequestDto;
import com.madgarage.api.dto.ReturnResponseDto;
import com.madgarage.api.enums.ReturnStatus;
import com.madgarage.api.model.ReturnRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.services.ReturnService;
import com.madgarage.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReturnResponseDto> createReturnRequest(Principal principal, @RequestBody ReturnRequestDto dto) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(mapToDto(returnService.createReturnRequest(user, dto)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReturnResponseDto>> getMyReturns(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(returnService.getMyReturns(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<List<ReturnResponseDto>> getAllReturns(@RequestParam(required = false) ReturnStatus status) {
        List<ReturnRequest> returns = (status != null) ? 
                returnService.getReturnsByStatus(status) : 
                returnService.getAllReturns();
        return ResponseEntity.ok(returns.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList()));
    }

    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ReturnResponseDto> approveReturn(Principal principal, @PathVariable Long id) {
        User admin = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(mapToDto(returnService.approveReturn(id, admin)));
    }

    @PutMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ReturnResponseDto> rejectReturn(Principal principal, @PathVariable Long id, @RequestParam String note) {
        User admin = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(mapToDto(returnService.rejectReturn(id, note, admin)));
    }

    @PutMapping("/admin/{id}/picked-up")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ReturnResponseDto> markPickedUp(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDto(returnService.markPickedUp(id)));
    }

    @PutMapping("/admin/{id}/finalize")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<ReturnResponseDto> finalizeRefund(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDto(returnService.finalizeRefund(id)));
    }

    private ReturnResponseDto mapToDto(ReturnRequest request) {
        ReturnResponseDto dto = new ReturnResponseDto();
        dto.setId(request.getId());
        dto.setOrderId(request.getOrder().getId());
        dto.setUserId(request.getUser().getId());
        dto.setCustomerName(request.getUser().getFirstName() + " " + request.getUser().getLastName());
        dto.setReason(request.getReason());
        dto.setRequestType(request.getRequestType());
        dto.setDescription(request.getDescription());
        dto.setImageUrls(request.getImageUrls());
        dto.setStatus(request.getStatus());
        dto.setReplacementOrderId(request.getReplacementOrderId());
        dto.setAdminNote(request.getAdminNote());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setResolvedAt(request.getResolvedAt());
        return dto;
    }
}
