package com.universalis.blog.domain.authentication.services;

import com.universalis.blog.domain.authentication.dtos.AuthenticationResponse;
import com.universalis.blog.domain.user.dtos.RegisterRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

    AuthenticationResponse authenticate(String email, String password);
    AuthenticationResponse refreshToken(String refreshTokenStr);
    UserDetails validateToken(String token);
    AuthenticationResponse registerAndAuthenticate(RegisterRequest registerRequest);
}
