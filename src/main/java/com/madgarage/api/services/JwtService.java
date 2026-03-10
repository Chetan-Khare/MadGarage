package com.madgarage.api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.madgarage.api.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // In a production app, this should be hidden in your application.properties file!
    private final String SECRET = "MadGarageSuperSecretKey2026!";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    // Creates the digital VIP wristband for the user
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole().name())
                .withClaim("userId", user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Valid for 7 Days
                .sign(algorithm);
    }

    // Reads the token to figure out who is making the request
    public String extractEmail(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return decodedJWT.getSubject();
    }

    // Checks if the token is valid and hasn't expired
    public boolean isTokenValid(String token, String userEmail) {
        String extractedEmail = extractEmail(token);
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        boolean isExpired = decodedJWT.getExpiresAt().before(new Date());

        return (extractedEmail.equals(userEmail) && !isExpired);
    }
}