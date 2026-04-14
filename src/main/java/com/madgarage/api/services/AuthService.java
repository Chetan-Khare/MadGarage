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
     * Verifies OTP, then finds existing user or returns a registration requirement.
     * Returns an AuthResponse containing either a login JWT or a registrationToken.
     */
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        if (!otpService.verifyOtp(request.getPhone(), request.getOtp())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }

        Optional<User> userOptional = userRepository.findByPhone(request.getPhone());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isActive()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
            }
            String token = jwtService.generateToken(user);
            log.info("[Auth] Existing user found. Returning token for userId: {}", user.getId());
            return AuthResponse.builder()
                    .token(token)
                    .message("OTP Login Successful!")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .build();
        } else {
            // First time login: require registration details (email/password)
            String regToken = jwtService.generateRegistrationToken(request.getPhone());
            log.info("[Auth] New user detected. Returning registrationToken.");
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
        log.info("[Auth] Login attempt initiated for canonicalized email: [{}]", email);
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (AuthenticationException e) {
            log.warn("[Auth] AuthenticationManager rejected credentials for [{}]. Reason: {}", email, e.getMessage());
            
            // Check if user exists and manually verify password for definitive diagnostic
            userRepository.findByEmail(email).ifPresentOrElse(
                u -> {
                    boolean matches = passwordEncoder.matches(request.getPassword(), u.getPassword());
                    log.info("[Auth] Diagnostic: User [{}] EXISTS. Manual password match check: {}", email, matches);
                },
                () -> log.info("[Auth] Diagnostic: User [{}] DOES NOT EXIST in database.", email)
            );
            
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[Auth] CRITICAL: Authentication SUCCEEDED but record for [{}] disappeared from DB midway!", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
                });

        if (!user.isActive()) {
            log.warn("[Auth] Login BLOCKED for deactivated account: [{}]", email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account has been deactivated. Access denied.");
        }

        log.info("[Auth] Login SUCCESSFUL for userId: {} [{}]", user.getId(), email);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .message("Login successful!")
                .userId(user.getId())
                .role(user.getRole().name())
                .build();
    }
}
