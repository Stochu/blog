package com.universalis.blog.services;

import com.universalis.blog.domain.dtos.AuthenticationResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

    AuthenticationResponse authenticate(String email, String password);
    AuthenticationResponse refreshToken(String refreshTokenStr);
    UserDetails validateToken(String token);
}
