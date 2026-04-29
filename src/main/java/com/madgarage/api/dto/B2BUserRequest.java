package com.madgarage.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class B2BUserRequest {
    @Size(max = 50, message = "First name too long")
    private String firstName;

    @Size(max = 50, message = "Last name too long")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    private String phone;
    private String city;
    private String address;
    private String floor;
    private String buildingName;
    private String pincode;
    private String state;
    private Double latitude;
    private Double longitude;
    private Boolean isTieUp;
}
