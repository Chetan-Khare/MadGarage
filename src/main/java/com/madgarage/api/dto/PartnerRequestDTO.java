package com.madgarage.api.dto;

import com.madgarage.api.model.PartnerRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRequestDTO {

    private Long id;

    @NotBlank(message = "Business name is mandatory")
    private String businessName;

    @NotBlank(message = "Contact name is mandatory")
    private String contactName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    @NotBlank(message = "Role selection is mandatory")
    @Pattern(regexp = "^(ROLE_SELLER|ROLE_GARAGE)$", message = "Invalid role selected")
    private String role;

    private String address;
    private String city;
    private String state;
    private String pincode;

    private PartnerRequest.RequestStatus status;
    private String internalNotes;
    private LocalDateTime createdAt;
}
