package com.madgarage.api.services;

import com.madgarage.api.dto.PartnerRequestDTO;
import com.madgarage.api.dto.PartnerRequestsSummaryResponse;
import com.madgarage.api.enums.Role;
import com.madgarage.api.model.PartnerRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.PartnerRequestRepository;
import com.madgarage.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerRequestService {

    private final PartnerRequestRepository partnerRequestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Public submission of a new partnership application.
     */
    @Transactional
    public PartnerRequest submitRequest(PartnerRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail().toLowerCase()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered as an active user.");
        }

        PartnerRequest request = PartnerRequest.builder()
                .businessName(dto.getBusinessName())
                .contactName(dto.getContactName())
                .email(dto.getEmail().toLowerCase())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .status(PartnerRequest.RequestStatus.PENDING)
                .build();

        return partnerRequestRepository.save(request);
    }

    /**
     * List all requests and global partnership stats for administrative review.
     */
    public PartnerRequestsSummaryResponse getRequestsSummary() {
        List<PartnerRequestDTO> requests = partnerRequestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        long totalActivePartners = userRepository.countByIsTieUpTrueAndIsActiveTrue();

        return PartnerRequestsSummaryResponse.builder()
                .requests(requests)
                .totalActivePartners(totalActivePartners)
                .build();
    }

    /**
     * Update internal notes for a request.
     */
    @Transactional
    public void updateNotes(Long id, String notes) {
        PartnerRequest request = findRequest(id);
        request.setInternalNotes(notes);
        partnerRequestRepository.save(request);
    }

    /**
     * Mark a request as "CONTACTED".
     */
    @Transactional
    public void markAsContacted(Long id) {
        PartnerRequest request = findRequest(id);
        request.setStatus(PartnerRequest.RequestStatus.CONTACTED);
        partnerRequestRepository.save(request);
    }

    /**
     * Approve a request: Creates a User account and marks request as APPROVED.
     */
    @Transactional
    public String approveRequest(Long id) {
        PartnerRequest request = findRequest(id);
        if (request.getStatus() == PartnerRequest.RequestStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is already approved.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is now claimed by another user.");
        }

        // Generate a secure temporary password
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        
        // Map Role
        Role role = Role.valueOf(request.getRole());

        // Split Contact Name for User record
        String[] nameParts = request.getContactName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[1] : "User";

        User newUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(role)
                .phone(request.getPhone())
                .city(request.getCity())
                .state(request.getState())
                .address(request.getAddress())
                .pincode(request.getPincode())
                .isActive(true)
                .build();

        userRepository.save(newUser);
        
        request.setStatus(PartnerRequest.RequestStatus.APPROVED);
        request.setInternalNotes(request.getInternalNotes() + "\n[System] Approved and account created on " + java.time.LocalDateTime.now());
        partnerRequestRepository.save(request);

        return tempPassword;
    }

    /**
     * Decline a request.
     */
    @Transactional
    public void rejectRequest(Long id) {
        PartnerRequest request = findRequest(id);
        request.setStatus(PartnerRequest.RequestStatus.REJECTED);
        partnerRequestRepository.save(request);
    }

    private PartnerRequest findRequest(Long id) {
        return partnerRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner request not found."));
    }

    private PartnerRequestDTO toDTO(PartnerRequest entity) {
        return PartnerRequestDTO.builder()
                .id(entity.getId())
                .businessName(entity.getBusinessName())
                .contactName(entity.getContactName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .role(entity.getRole())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .pincode(entity.getPincode())
                .status(entity.getStatus())
                .internalNotes(entity.getInternalNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
