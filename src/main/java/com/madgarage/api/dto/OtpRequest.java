package com.madgarage.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
    @NotBlank(message = "Phone number is required")
    private String phone;
}
