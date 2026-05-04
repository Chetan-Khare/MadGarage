package com.madgarage.api.controller;

import com.madgarage.api.dto.UserProfileResponse;
import com.madgarage.api.dto.UserProfileUpdateRequest;
import com.madgarage.api.model.User;
import com.madgarage.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * UserController is a thin HTTP routing layer.
 * All business logic lives in UserService.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(userService.toProfileResponse(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Principal principal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse response = userService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/profile-image", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadProfileImage(
            Principal principal,
            @RequestParam("image") MultipartFile image) {
        String fileUrl = userService.uploadProfileImage(principal.getName(), image);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping("/profile-image/base64")
    public ResponseEntity<?> uploadProfileImageBase64(
            Principal principal,
            @RequestBody com.madgarage.api.dto.ProfileImageBase64Request request) {
        String fileUrl = userService.uploadProfileImageBase64(principal.getName(), request);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<?> deleteProfileImage(Principal principal) {
        userService.deleteProfileImage(principal.getName());
        return ResponseEntity.ok("Profile image removed.");
    }

    @PutMapping("/push-token")
    public ResponseEntity<?> savePushToken(
            Principal principal,
            @RequestBody java.util.Map<String, String> body) {
        String token = body.get("expoPushToken");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Missing expoPushToken field");
        }
        userService.savePushToken(principal.getName(), token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/garages")
    public ResponseEntity<java.util.List<UserProfileResponse>> getGaragesByCity(@RequestParam String city) {
        return ResponseEntity.ok(userService.getTieUpGaragesByCity(city));
    }
}
