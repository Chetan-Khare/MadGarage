package com.madgarage.api.dto;

public record AiResponseDto(String make,
                            String model,
                            Integer year,
                            String fuel,      // Added Fuel
                            String trim,
                            String engine,    // Added Engine
                            String category,
                            String message,   // The AI's "voice"
                            boolean needsMoreInfo) {}
