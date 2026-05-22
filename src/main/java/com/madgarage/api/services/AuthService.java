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
    private final RateLimitingService rateLimitingService;

    /**
     * Generates and logs an OTP for the given phone number.
     * Enforces strict rate limits by Phone (Cost Control) and IP (Bot Prevention).
     */
    public void sendOtp(OtpRequest request, String ip) {
        io.github.bucket4j.ConsumptionProbe phoneProbe = rateLimitingService.probeOtpByPhone(request.getPhone());
        if (!phoneProbe.isConsumed()) {
            long waitTime = phoneProbe.getNanosToWaitForRefill() / 1_000_000_000L;
            log.warn("[Auth] OTP Rate Limit EXCEEDED for phone: {}. Wait: {}s", request.getPhone(), waitTime);
            throw new com.madgarage.api.exceptions.RateLimitExceededException("Too many requests for this number. Please wait.", waitTime);
        }

        io.github.bucket4j.ConsumptionProbe ipProbe = rateLimitingService.probeOtpByIp(ip);
        if (!ipProbe.isConsumed()) {
            long waitTime = ipProbe.getNanosToWaitForRefill() / 1_000_000_000L;
            log.warn("[Auth] OTP Rate Limit EXCEEDED for IP: {}. Wait: {}s", ip, waitTime);
            throw new com.madgarage.api.exceptions.RateLimitExceededException("Too many requests from this device. Please wait.", waitTime);
        }

        otpService.generateOtp(request.getPhone());
    }

    /**
     * Verifies OTP, then finds existing user or returns a registration requirement.
     * Returns an AuthResponse containing either a login JWT or a registrationToken.
     */
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        String phone = request.getPhone();
        
        io.github.bucket4j.EstimationProbe probe = rateLimitingService.probeAuthAttempt(phone);
        if (!probe.canBeConsumed()) {
            long waitTime = probe.getNanosToWaitForRefill() / 1_000_000_000L;
            log.warn("[Auth] Brute-force protection: Blocking attempt for phone: {}. Wait: {}s", phone, waitTime);
            throw new com.madgarage.api.exceptions.RateLimitExceededException("Too many failed attempts. Account locked temporarily.", waitTime);
        }

        if (!otpService.verifyOtp(phone, request.getOtp())) {
            rateLimitingService.recordAuthFailure(phone);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }

        rateLimitingService.resetAuthAttempts(phone);

        Optional<User> userOptional = userRepository.findByPhone(request.getPhone());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
            }
            String token = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .message("OTP Login Successful!")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .build();
        } else {
            // First time login: require registration details (email/password)
            String regToken = jwtService.generateRegistrationToken(request.getPhone());

            return AuthResponse.builder()
                    .message("Verification successful! Please complete your profile.")
                    .requiresRegistration(true)
                    .registrationToken(regToken)
                    .build();
        }
    }

    /**
     * Completes registration for a verified phone number.
     * Validates account details and creates the final user record.
     */
    @org.springframework.transaction.annotation.Transactional
    public AuthResponse completeRegistration(CompleteRegistrationRequest request) {
        String phone;
        try {
            phone = jwtService.extractPhoneFromRegistrationToken(request.getRegistrationToken());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired registration session.");
        }

        if (userRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken.");
        }

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already exists for this phone number.");
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(phone)
                .role(Role.ROLE_CUSTOMER)
                .isActive(true)
                .build();

        newUser = userRepository.save(newUser);
        String token = jwtService.generateToken(newUser);
        
        return AuthResponse.builder()
                .token(token)
                .message("Account created successfully!")
                .userId(newUser.getId())
                .role(newUser.getRole().name())
                .build();
    }

    /**
     * Registers a new CUSTOMER account and returns a JWT so the user
     * is logged-in immediately after sign-up.
     */
    @org.springframework.transaction.annotation.Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered!");
        }

        // H-7 FIX: Ensure phone is handled (even if null for email-only registration).
        // If the client starts sending phone in RegisterRequest, validate it here.
        // For now, allow email-only registration but prevent duplicate null phones if DB constraint exists.
        String phone = request.getPhone(); // Requires adding phone to RegisterRequest
        if (phone != null && !phone.isBlank()) {
            if (userRepository.findByPhone(phone).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number is already registered!");
            }
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(newUser);
        String token = jwtService.generateToken(newUser);
        return AuthResponse.builder()
                .token(token)
                .message("Registration successful!")
                .userId(newUser.getId())
                .role(newUser.getRole().name())
                .build();
    }

    /**
     * Authenticates an existing user by email/password using Spring Security's
     * AuthenticationManager (constant-time verification to mitigate timing attacks).
     */
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        
        io.github.bucket4j.EstimationProbe probe = rateLimitingService.probeAuthAttempt(email);
        if (!probe.canBeConsumed()) {
            long waitTime = probe.getNanosToWaitForRefill() / 1_000_000_000L;
            log.warn("[Auth] Brute-force protection: Blocking login for email: [{}]. Wait: {}s", email, waitTime);
            throw new com.madgarage.api.exceptions.RateLimitExceededException("Account locked due to multiple failed attempts.", waitTime);
        }
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (AuthenticationException e) {
            rateLimitingService.recordAuthFailure(email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        rateLimitingService.resetAuthAttempts(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[Auth] CRITICAL: Authentication SUCCEEDED but record for [{}] disappeared from DB midway!", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
                });

        if (!user.isActive()) {
            log.warn("[Auth] Login BLOCKED for deactivated account: [{}]", email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
        }

        log.info("[Auth] Login SUCCESSFUL for userId: {}", user.getId());
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .message("Login successful!")
                .userId(user.getId())
                .role(user.getRole().name())
                .build();
    }
}
