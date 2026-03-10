package com.madgarage.api.dto;

public record AuthResponseDto(String token,
                              String role,
                              String message) {}
