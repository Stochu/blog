package com.universalis.blog.domain.user.services.impl;

import com.universalis.blog.domain.user.dtos.RegisterRequest;
import com.universalis.blog.domain.user.dtos.UserDTO;
import com.universalis.blog.domain.user.entities.User;
import com.universalis.blog.domain.user.repositories.UserRepository;
import com.universalis.blog.domain.user.services.UserService;
import com.universalis.blog.exceptions.UserRegistrationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Override
    public UserDTO registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new UserRegistrationException("Password and confirmation password do not match");
        }
        if (emailExists(registerRequest.getEmail())) {
            throw new UserRegistrationException("Email address is already registered");
        }
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        return UserDTO.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim()).isPresent();
    }
}
