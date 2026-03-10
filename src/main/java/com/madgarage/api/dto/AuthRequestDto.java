package com.madgarage.api.dto;

public record AuthRequestDto(String email,
                             String firstName,
                             String lastName,
                             String password,
                             String role) {}
