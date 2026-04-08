package com.madgarage.api.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * RoleConverter maps between the database string and the Role enum.
 * It provides backward compatibility by normalizing legacy values (e.g., "ADMIN" -> ROLE_ADMIN)
 * during the fetch operation.
 */
@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) return null;
        return role.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        String normalized = dbData.toUpperCase();
        
        // P0 FIX: Handle legacy values from database (e.g., "ADMIN", "SELLER")
        // by adding the "ROLE_" prefix if it's missing.
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Fallback to CUSTOMER if role is unknown or corrupt
            return Role.ROLE_CUSTOMER;
        }
    }
}
