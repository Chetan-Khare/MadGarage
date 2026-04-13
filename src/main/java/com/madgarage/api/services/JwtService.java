package com.madgarage.api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.madgarage.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // SEC-05 FIX: Secret is now injected from application.yml which reads from
    // the JWT_SECRET environment variable. Never hardcode secrets in source code.
    @Value("${app.jwt.secret}")
    private String SECRET;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET);
    }

    // Creates the digital VIP wristband for the user
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole().name())
                .withClaim("userId", user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Valid for 7 Days
                .sign(getAlgorithm());
    }

    // Reads the token to figure out who is making the request
    public String extractEmail(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getSubject();
    }

    // Reads the role from the token
    public String extractRole(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
        return decodedJWT.getClaim("role").asString();
    }

}