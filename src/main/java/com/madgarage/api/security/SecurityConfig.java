package com.madgarage.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter; // ADD THIS

    public SecurityConfig(JwtFilter jwtFilter) { // ADD THIS CONSTRUCTOR
        this.jwtFilter = jwtFilter;
    }

    // 1. Password Encrypter (Scrambles passwords before saving to the database)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. The Rule Book (The Bouncer)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF because mobile apps don't use it
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions, only JWTs!
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC ROUTES (Anyone can access these)
                        .requestMatchers("/api/auth/**").permitAll() // The Login and Register endpoints
                        .requestMatchers("/api/assistant/**").permitAll() // AI Chat is public
                        .requestMatchers("/seller_dashboard.html").permitAll() // Temporary HTML access
                        .requestMatchers("/uploads/**").permitAll() // Allow images to load
                        .requestMatchers("/api/products/garage").hasAuthority("GARAGE")
                        .requestMatchers("/api/products/**").permitAll()



                        // SECURE ROUTES (Must have a JWT token to access)
                        .anyRequest().authenticated()
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}