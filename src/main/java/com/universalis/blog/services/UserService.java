package com.universalis.blog.services;

import com.universalis.blog.domain.entities.User;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID id);
}

