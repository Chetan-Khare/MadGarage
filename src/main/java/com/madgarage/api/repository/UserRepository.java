package com.madgarage.api.repository;

import com.madgarage.api.enums.Role;
import com.madgarage.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // We will need this specific method later for Spring Security and JWT login
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    // Admin Dashboard Counters
    long countByRole(Role role);
}