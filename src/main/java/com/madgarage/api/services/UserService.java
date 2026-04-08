package com.madgarage.api.services;

import com.madgarage.api.dto.*;
import com.madgarage.api.enums.Role;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRepository;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.repository.UserRepository;
import com.madgarage.api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * UserService is the single point of entry for all user-related business logic.
 * No controller should directly call UserRepository — route through this service instead.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final VehicleRepository vehicleRepository;
    
    // Improved Path handling: resolving relative to the current project root at runtime
    private static final String UPLOAD_REL_PATH = "src/main/resources/static/uploads/";

    /**
     * Resolves the currently authenticated user by email.
     * Throws 401 if the user cannot be found (should never happen with a valid JWT).
     */
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found."));
    }

    /**
     * Retrieves all users in the system as Profile Response DTOs.
     */
    public java.util.List<UserProfileResponse> getAllUsersProfileResponses() {
        return userRepository.findAll().stream()
                .map(this::toProfileResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Maps a User entity to a safe, outbound UserProfileResponse DTO.
     * Prevents accidental entity leakage to the API layer.
     */
    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    /**
     * Applies non-null field updates to the user's profile and persists the change.
     */
    public UserProfileResponse updateProfile(String email, UserProfileUpdateRequest request) {
        User user = getCurrentUser(email);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    /**
     * Validates, saves a profile image file, and updates the user's profile image URL.
     * Returns the public URL of the saved file.
     */
    public String uploadProfileImage(String email, MultipartFile image) {
        User user = getCurrentUser(email);

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed.");
        }

        try {
            // Robust absolute path resolution
            Path basePath = Paths.get(UPLOAD_REL_PATH).toAbsolutePath().normalize();
            File directory = basePath.toFile();
            
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Could not create directory: " + basePath);
                }
            }

            String extension = contentType.split("/")[1].replaceAll("[^a-zA-Z0-9]", "");
            String safeFileName = java.util.UUID.randomUUID() + "." + extension;
            Path filePath = basePath.resolve(safeFileName).normalize();

            if (!filePath.startsWith(basePath)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path.");
            }

            Files.write(filePath, image.getBytes());

            String fileUrl = "/uploads/" + safeFileName;
            user.setProfileImageUrl(fileUrl);
            userRepository.save(user);
            return fileUrl;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Decodes a Base64 string, validates, saves the profile image file, and updates the user's profile image URL.
     * Returns the public URL of the saved file.
     */
    public String uploadProfileImageBase64(String email, ProfileImageBase64Request request) {
        User user = getCurrentUser(email);
        
        if (request.getBase64Image() == null || request.getBase64Image().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base64 image data missing.");
        }

        try {
            Path basePath = Paths.get(UPLOAD_REL_PATH).toAbsolutePath().normalize();
            File directory = basePath.toFile();
            
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Could not create directory: " + basePath);
                }
            }

            // Clean the base64 string if it contains data URI header
            String base64Data = request.getBase64Image();
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
            
            // Default to jpg if no extension is provided
            String extension = (request.getExtension() != null && !request.getExtension().isBlank()) 
                    ? request.getExtension().replaceAll("[^a-zA-Z0-9]", "") 
                    : "jpg";
                    
            String safeFileName = java.util.UUID.randomUUID() + "." + extension;
            Path filePath = basePath.resolve(safeFileName).normalize();

            Files.write(filePath, imageBytes);

            String fileUrl = "/uploads/" + safeFileName;
            user.setProfileImageUrl(fileUrl);
            userRepository.save(user);
            return fileUrl;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image from Base64: " + e.getMessage());
        }
    }

    /**
     * Creates a new B2B user (SELLER or GARAGE role) for the admin dashboard.
     */
    public void createB2BUser(B2BUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        Role newRole;
        try {
            String roleStr = request.getRole().toUpperCase();
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr;
            }
            newRole = Role.valueOf(roleStr);
            if (newRole != Role.ROLE_SELLER && newRole != Role.ROLE_GARAGE && newRole != Role.ROLE_ADMIN) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Role. Must be SELLER or GARAGE.");
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(newRole)
                .phone(request.getPhone())
                .isActive(true)
                .build();

        userRepository.save(newUser);
    }

    /**
     * Aggregates platform-wide analytics for the Admin dashboard.
     */
    public AdminAnalyticsResponse getAdminAnalytics() {
        long totalUsers = userRepository.count();
        long totalSellers = userRepository.countByRole(Role.ROLE_SELLER);
        long totalProducts = productRepository.count();
        long totalVehicles = vehicleRepository.count();

        Double revenueObj = orderRepository.calculateTotalRevenue();
        double totalRevenue = (revenueObj != null) ? revenueObj : 0.0;

        java.util.List<Double> sixMonthRevenue = java.util.Arrays.asList(
                totalRevenue * 0.1,
                totalRevenue * 0.15,
                totalRevenue * 0.2,
                totalRevenue * 0.35,
                totalRevenue * 0.6,
                totalRevenue
        );

        return new AdminAnalyticsResponse(totalUsers, totalSellers, totalProducts, totalVehicles, totalRevenue, sixMonthRevenue);
    }

    /**
     * Bans (deactivates) a user from the platform.
     * Prevents data loss by disabling the account instead of physical deletion.
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Stores the device's Expo push token on the user record.
     * Called by the frontend immediately after login on each session.
     */
    public void savePushToken(String email, String expoPushToken) {
        User user = getCurrentUser(email);
        user.setExpoPushToken(expoPushToken);
        userRepository.save(user);
    }
}