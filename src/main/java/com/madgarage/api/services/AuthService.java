package com.madgarage.api.services;

import com.madgarage.api.dto.*;
import com.madgarage.api.enums.Role;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * AuthService owns all authentication business logic:
 * OTP flow, registration, and login.
 * AuthController is a thin HTTP router that delegates entirely to this class.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    /**
     * Generates and logs an OTP for the given phone number.
     * Returns the OTP string so the controller can decide how to surface it
     * (currently echoed in response for test purposes).
     */
    public String sendOtp(OtpRequest request) {
        return otpService.generateOtp(request.getPhone());
    }

    /**
     * Verifies OTP, then finds or auto-creates a CUSTOMER account.
     * Returns an AuthResponse containing the JWT token and user metadata.
     */
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        if (!otpService.verifyOtp(request.getPhone(), request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }

        Optional<User> userOptional = userRepository.findByPhone(request.getPhone());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
            }
        } else {
            // Auto-create new Customer account for unrecognised phone numbers
            String randomPassword = java.util.UUID.randomUUID().toString();
            user = User.builder()
                    .firstName("Customer")
                    .lastName(request.getPhone())
                    .email(request.getPhone() + "@madgarage.com") // Placeholder
                    .password(passwordEncoder.encode(randomPassword))
                    .phone(request.getPhone())
                    .role(Role.ROLE_CUSTOMER)
                    .isActive(true)
                    .build();
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "OTP Login Successful!", user.getId(), user.getRole().name());
    }

    /**
     * Registers a new CUSTOMER account and returns a JWT so the user
     * is logged-in immediately after sign-up.
     */
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(newUser);
        String token = jwtService.generateToken(newUser);
        return new AuthResponse(token, "Registration successful!", newUser.getId(), newUser.getRole().name());
    }

    /**
     * Authenticates an existing user by email/password using Spring Security's
     * AuthenticationManager (constant-time verification to mitigate timing attacks).
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        log.debug("Login attempt for: [{}]", email);
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (AuthenticationException e) {
            log.debug("Authentication failed for: [{}] | Reason: {}", email, e.getMessage());
            // Check if user even exists in DB
            boolean exists = userRepository.findByEmail(email).isPresent();
            log.debug("Does user exist in DB? {}", exists);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("CRITICAL: User authenticated but not found in DB: [{}]", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
                });

        if (!user.isActive()) {
            log.warn("Login attempt for deactivated user: [{}]", email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
        }

        log.debug("Login successful for: [{}] | Role: {}", email, user.getRole());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Login successful!", user.getId(), user.getRole().name());
    }
}
