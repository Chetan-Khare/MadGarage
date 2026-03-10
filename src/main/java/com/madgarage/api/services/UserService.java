package com.madgarage.api.services;

import com.madgarage.api.model.User;
import com.madgarage.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok creates a constructor for our repository automatically
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        // TODO: We will add password encoding (BCrypt) here when we configure Spring Security
        return userRepository.save(user);
    }
}