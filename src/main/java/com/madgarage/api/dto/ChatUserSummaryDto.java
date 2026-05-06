package com.madgarage.api.dto;

/**
 * DTO for admin audit view to prevent leaking full User entity PII.
 */
public record ChatUserSummaryDto(
    Long id,
    String fullName,
    String email,
    String phone,
    String role
) {}
