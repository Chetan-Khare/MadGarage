package com.madgarage.api.dto;

public record AiResponseDto(String make,
                            String model,
                            Integer year,
                            String trim,      // Added Trim
                            String category,
                            String message,   // The AI's "voice" (e.g., "Is that the SX or SX(O)?")
                            boolean needsMoreInfo) {}
