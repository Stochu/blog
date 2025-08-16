package com.universalis.blog.domain.user.services;

import com.universalis.blog.domain.user.dtos.RegisterRequest;
import com.universalis.blog.domain.user.dtos.UserDTO;
import com.universalis.blog.domain.user.entities.User;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID id);
    UserDTO registerUser(RegisterRequest registerRequest);
    boolean emailExists(String email);
}

