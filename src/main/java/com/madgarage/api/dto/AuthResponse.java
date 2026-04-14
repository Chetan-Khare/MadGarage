package com.madgarage.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("role")
    private String role;
    
    // P1 REGISTRATION FLOW:
    @Builder.Default
    @JsonProperty("requiresRegistration")
    private boolean requiresRegistration = false;
    
    @JsonProperty("registrationToken")
    private String registrationToken; // Temporary token containing phone for new users
}
