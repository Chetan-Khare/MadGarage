package com.madgarage.api.controller;

import com.madgarage.api.dto.AdminAnalyticsResponse;
import com.madgarage.api.enums.Role;
import com.madgarage.api.model.Order;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.OrderRepository;
import com.madgarage.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, OrderRepository orderRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsResponse> getAnalytics() {
        long totalUsers = userRepository.count();
        long totalSellers = userRepository.countByRole(Role.SELLER);

        // Sum the totalAmount of all orders in the database
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // In a real app we would write a SQL query GROUP BY MONTH(createdAt).
        // For now, we will provide a mocked array simulating real growth for the chart:
        List<Double> sixMonthRevenue = Arrays.asList(
                totalRevenue * 0.1, // 6 months ago
                totalRevenue * 0.15, // 5 months ago
                totalRevenue * 0.2, // 4 months ago
                totalRevenue * 0.35, // 3 months ago
                totalRevenue * 0.6, // 2 months ago
                totalRevenue // This month
        );

        AdminAnalyticsResponse response = new AdminAnalyticsResponse(
                totalUsers,
                totalSellers,
                totalRevenue,
                sixMonthRevenue);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createB2BUser(@RequestBody B2BUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered!");
        }

        // Only allow creating Garage or Seller users from the Admin dashboard
        Role newRole;
        try {
            newRole = Role.valueOf(request.getRole().toUpperCase());
            if (newRole != Role.SELLER && newRole != Role.GARAGE) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Role. Must be SELLER or GARAGE");
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(newRole)
                .isActive(true)
                .build();

        userRepository.save(newUser);

        return ResponseEntity.ok(request.getRole() + " Account created successfully!");
    }
}

class B2BUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
