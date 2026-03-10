package com.madgarage.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // The master key used to sign the digital ID cards.
    // (In a real production app, we would hide this inside application.yml)
    private final String SECRET = "MadGarageSuperSecretKey2026!";

    // Generates a digital ID valid for 24 hours
    public String generateToken(String email, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .sign(Algorithm.HMAC256(SECRET));
    }
    // Reads the role from the ID card
    public String extractRole(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getClaim("role").asString();
    }

    // Reads the ID card to figure out who is making the request
    public String extractEmail(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getSubject();
    }
}