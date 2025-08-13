package com.universalis.blog.domain.auth.services.impl;

import com.universalis.blog.domain.auth.entities.User;
import com.universalis.blog.domain.auth.repositories.UserRepository;
import com.universalis.blog.domain.auth.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }
}
