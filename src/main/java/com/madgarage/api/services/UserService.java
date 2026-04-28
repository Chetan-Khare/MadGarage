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
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final VehicleRepository vehicleRepository;
    private final FileStorageService fileStorageService;
    private final JwtService jwtService;

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
                .active(user.isActive())
                .city(user.getCity())
                .address(user.getAddress())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .tieUp(user.getIsTieUp() != null ? user.getIsTieUp() : false)
                .build();
    }

    /**
     * Applies non-null field updates to the user's profile and persists the change.
     */
    @Transactional
    public UserProfileResponse updateProfile(String email, UserProfileUpdateRequest request) {
        User user = getCurrentUser(email);
        boolean emailChanged = false;

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            // P1 FIX: Check for email conflicts before save to avoid DB-level DataIntegrityViolation
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken by another account.");
                }
                emailChanged = true;
            }
            user.setEmail(newEmail);
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            String newPhone = request.getPhone().trim();
            if (!newPhone.equals(user.getPhone())) {
                if (userRepository.findByPhone(newPhone).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number is already registered to another account.");
                }
                user.setPhone(newPhone);
            }
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            log.info("[Identity] Updating password for user: {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getLatitude() != null) {
            user.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            user.setLongitude(request.getLongitude());
        }

        log.info("[Identity] Persisting profile changes for userId: {} | New Email: {}", user.getId(), user.getEmail());
        user = userRepository.save(user);
        
        UserProfileResponse response = toProfileResponse(user);
        if (emailChanged) {
            log.info("[Identity] Email changed, generating a refreshed session token for user: {}", user.getEmail());
            response.setToken(jwtService.generateToken(user));
        }
        return response;
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
            String extension = contentType.split("/")[1].replaceAll("[^a-zA-Z0-9]", "");
            String fileUrl = fileStorageService.saveImage(image.getBytes(), extension);
            user.setProfileImageUrl(fileUrl);
            userRepository.save(user);
            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to upload image for user {}: ", email, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image. Please try again later.");
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
            String base64Data = request.getBase64Image();
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
            
            String extension = (request.getExtension() != null && !request.getExtension().isBlank()) 
                    ? request.getExtension().replaceAll("[^a-zA-Z0-9]", "").toLowerCase() 
                    : "jpg";
            
            java.util.List<String> allowedExtensions = java.util.Arrays.asList("jpg", "jpeg", "png", "webp");
            if (!allowedExtensions.contains(extension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image extension: " + extension);
            }
                    
            String fileUrl = fileStorageService.saveImage(imageBytes, extension);
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
    public void createB2BUser(B2BUserRequest request, User currentUser) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required for new accounts.");
        }

        if (userRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number is already associated with another account!");
        }

        Role newRole;
        try {
            String roleStr = request.getRole().toUpperCase();
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr;
            }
            newRole = Role.valueOf(roleStr);
            if (newRole != Role.ROLE_SELLER && newRole != Role.ROLE_GARAGE && newRole != Role.ROLE_ADMIN && newRole != Role.ROLE_WORKER) {
                throw new IllegalArgumentException();
            }
            if (currentUser.getRole() == Role.ROLE_WORKER && (newRole == Role.ROLE_ADMIN || newRole == Role.ROLE_WORKER)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Workers cannot create Administrative or Worker accounts.");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Role. Authorized roles are SELLER, GARAGE, WORKER, or ADMIN.");
        }

        // P2 FIX: Removed 'password123' security fallback. Admins must explicitly provide credentials for new accounts.
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A temporary password is required for new accounts.");
        }
        String rawPassword = request.getPassword();

        User newUser = User.builder()
                .firstName(request.getFirstName() != null ? request.getFirstName() : "Operator")
                .lastName(request.getLastName() != null ? request.getLastName() : "User")
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .role(newRole)
                .phone(request.getPhone())
                .city(request.getCity())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isTieUp(request.getIsTieUp() != null ? request.getIsTieUp() : false)
                .isActive(true)
                .build();

        userRepository.save(newUser);
    }

    /**
     * Updates an existing user's record from the Administrative control panel.
     */
    @Transactional
    public void updateUserByAdmin(Long id, B2BUserRequest request, User currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if (currentUser.getRole() == Role.ROLE_WORKER && (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_WORKER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Workers cannot modify Administrative or Worker accounts.");
        }

        // P1 REQ: Names and Password are now STATIC for Admins. Only Email and Phone can be edited.
        
        // Handle Email unique constraint
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New email is already taken.");
            }
            user.setEmail(newEmail);
        }

        // Handle Phone unique constraint
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New phone number is already taken.");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getLatitude() != null) user.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) user.setLongitude(request.getLongitude());
        if (request.getIsTieUp() != null) user.setIsTieUp(request.getIsTieUp());

        log.info("[Admin] Persisting identity revision for userId: {} by administrative action. New Email: {}", id, user.getEmail());
        userRepository.save(user);
    }

    /**
     * Aggregates platform-wide analytics for the Admin dashboard.
     */
    public AdminAnalyticsResponse getAdminAnalytics() {
        long totalUsers = userRepository.countByIsActiveTrue();
        long totalSellers = userRepository.countByRoleAndIsActiveTrue(Role.ROLE_SELLER);
        long totalProducts = productRepository.count();
        long totalVehicles = vehicleRepository.count();

        Double revenueObj = orderRepository.calculateTotalRevenue();
        double totalRevenue = (revenueObj != null) ? revenueObj : 0.0;

        java.util.List<Object[]> monthlyData = orderRepository.getMonthlyRevenueForLastSixMonths();
        java.util.List<Double> sixMonthRevenue = monthlyData.stream()
                .map(row -> row[0] != null ? ((Number) row[0]).doubleValue() : 0.0)
                .collect(java.util.stream.Collectors.toList());

        // Pad with zeros if less than 6 months of data
        while (sixMonthRevenue.size() < 6) {
            sixMonthRevenue.add(0, 0.0);
        }

        return new AdminAnalyticsResponse(totalUsers, totalSellers, totalProducts, totalVehicles, totalRevenue, sixMonthRevenue);
    }

    /**
     * Bans (deactivates) a user from the platform.
     * Prevents data loss by disabling the account instead of physical deletion.
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        
        long timestamp = System.currentTimeMillis();
        
        // Scramble unique identifiers to free them up for new accounts
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail() + "_DEACTIVATED_" + timestamp);
        }
        if (user.getPhone() != null) {
            user.setPhone(user.getPhone() + "_DEACT_" + timestamp);
        }
        
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Restores a deactivated user record to active status.
     * Reverses the identifier scrambling and enables platform access.
     */
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        if (user.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operator is already active.");
        }

        // Restore Email
        String originalEmail = user.getEmail();
        if (originalEmail != null && originalEmail.contains("_DEACTIVATED_")) {
            originalEmail = originalEmail.substring(0, originalEmail.indexOf("_DEACTIVATED_"));
            if (userRepository.findByEmail(originalEmail).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot restore: Email " + originalEmail + " is now claimed by another active user.");
            }
            user.setEmail(originalEmail);
        }

        // Restore Phone
        String originalPhone = user.getPhone();
        if (originalPhone != null && originalPhone.contains("_DEACT_")) {
            originalPhone = originalPhone.substring(0, originalPhone.indexOf("_DEACT_"));
            if (userRepository.findByPhone(originalPhone).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot restore: Phone number is now claimed by another active user.");
            }
            user.setPhone(originalPhone);
        }

        user.setActive(true);
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

    /**
     * Fetches all tie-up garages for a specific city.
     * Core logic for the city-based visibility feature.
     */
    public java.util.List<UserProfileResponse> getTieUpGaragesByCity(String city) {
        if (city == null || city.isBlank()) return new java.util.ArrayList<>();
        return userRepository.findByRoleAndCityIgnoreCaseAndIsTieUpTrueAndIsActiveTrue(Role.ROLE_GARAGE, city.trim()).stream()
                .map(this::toProfileResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}