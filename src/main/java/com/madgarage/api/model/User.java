package com.madgarage.api.model;

import com.madgarage.api.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// ARCH-05 FIX: Added index on phone — used on every OTP login (was a full table scan before)
@Table(name = "users", indexes = {
    @Index(name = "idx_user_phone", columnList = "phone")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;

    @Column(nullable = false)
    private Role role; // This handles ROLE_ADMIN, ROLE_SELLER, or ROLE_CUSTOMER

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @Column(unique = true)
    private String phone;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "expo_push_token")
    private String expoPushToken;

    private String city;
    private String address;
    private Double latitude;
    private Double longitude;

    @Builder.Default
    private Boolean isTieUp = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }
}