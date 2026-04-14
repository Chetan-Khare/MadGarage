package com.madgarage.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String profileImageUrl;
    private boolean active;
    private String token; // Refreshed session token for email updates
}
