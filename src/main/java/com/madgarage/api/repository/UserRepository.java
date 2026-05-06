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
    long countByIsActiveTrue();
    long countByRoleAndIsActiveTrue(Role role);
    long countByIsTieUpTrueAndIsActiveTrue();

    java.util.List<User> findByRoleAndCityIgnoreCaseAndIsTieUpTrueAndIsActiveTrue(Role role, String city);

    @org.springframework.data.jpa.repository.Query(value = "SELECT u.* FROM users u JOIN (SELECT user_id, MAX(created_at) as last_msg FROM chat_messages GROUP BY user_id) latest ON u.id = latest.user_id ORDER BY latest.last_msg DESC", nativeQuery = true)
    java.util.List<User> findUsersWithChatHistory();
}