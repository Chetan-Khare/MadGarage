package com.madgarage.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @Size(max = 10, message = "First name too long")
    private String firstName;

    @Size(max = 10, message = "Last name too long")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, max = 12, message = "Password must be between 6 and 12 characters")
    private String password;

    private String address;
    private String city;
    private String phone;
    private Double latitude;
    private Double longitude;
}
