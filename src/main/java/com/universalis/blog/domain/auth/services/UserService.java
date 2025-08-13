package com.universalis.blog.domain.auth.services;

import com.universalis.blog.domain.auth.entities.User;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID id);
}

