package com.madgarage.api.controller;

import com.madgarage.api.dto.*;
import com.madgarage.api.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController is a thin HTTP routing layer.
 * All business logic lives in AuthService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(jakarta.servlet.http.HttpServletRequest httpRequest, @Valid @RequestBody OtpRequest request) {
        String clientIp = getClientIp(httpRequest);
        authService.sendOtp(request, clientIp);
        return ResponseEntity.ok("OTP sent successfully to " + request.getPhone());
    }

    private String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) return request.getRemoteAddr();
        return xfHeader.split(",")[0].trim();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.verifyOtp(request);
        if (authResponse.getToken() != null) attachAuthCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        if (authResponse.getToken() != null) attachAuthCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        if (authResponse.getToken() != null) attachAuthCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody CompleteRegistrationRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.completeRegistration(request);
        if (authResponse.getToken() != null) attachAuthCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(jakarta.servlet.http.HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("mg_auth", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out of session.");
    }

    private void attachAuthCookie(jakarta.servlet.http.HttpServletResponse response, String token) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("mg_auth", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // SET TO TRUE IN PRODUCTION (Requires HTTPS)
        cookie.setAttribute("SameSite", "Lax");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 Days
        response.addCookie(cookie);
    }
}
