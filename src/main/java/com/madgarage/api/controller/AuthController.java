package com.madgarage.api.controller;

import com.madgarage.api.enums.Role;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.UserRepository;
import com.madgarage.api.services.JwtService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Check if the email is already in use
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered!");
        }

        // 2. Create the new Customer
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encrypt their password!
                .role(Role.CUSTOMER) // Automatically assign them as a Customer
                .isActive(true)
                .build();

        userRepository.save(newUser);

        // 3. Generate their login token so they don't have to log in immediately after signing up
        String token = jwtService.generateToken(newUser);
        return ResponseEntity.ok(new AuthResponse(token, "Registration successful!", newUser.getId(), newUser.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Find the user by their email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 2. Safely compare the typed password against the encrypted database password
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // 3. Generate and return the VIP token
                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(new AuthResponse(token, "Login successful!", user.getId(), user.getRole().name()));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    }
}

// --- DTOs (Data Transfer Objects) to map the incoming/outgoing JSON ---
@Data
class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}

@Data
class LoginRequest {
    private String email;
    private String password;
}

@Data
class AuthResponse {
    private String token;
    private String message;
    private Long userId;
    private String role;

    public AuthResponse(String token, String message, Long userId, String role) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.role = role;
    }
}