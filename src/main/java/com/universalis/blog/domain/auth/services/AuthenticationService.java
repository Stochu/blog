package com.universalis.blog.domain.auth.services;

import com.universalis.blog.domain.auth.dtos.AuthenticationResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

    AuthenticationResponse authenticate(String email, String password);
    AuthenticationResponse refreshToken(String refreshTokenStr);
    UserDetails validateToken(String token);
}
