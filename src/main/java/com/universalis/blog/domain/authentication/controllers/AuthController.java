package com.universalis.blog.domain.authentication.controllers;

import com.universalis.blog.domain.authentication.dtos.AuthenticationResponse;
import com.universalis.blog.domain.authentication.dtos.LoginRequest;
import com.universalis.blog.domain.authentication.dtos.LogoutRequest;
import com.universalis.blog.domain.authentication.dtos.TokenRefreshRequest;
import com.universalis.blog.domain.user.dtos.RegisterRequest;
import com.universalis.blog.security.BlogUserDetails;
import com.universalis.blog.domain.authentication.services.AuthenticationService;
import com.universalis.blog.domain.authentication.services.impl.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthenticationResponse response = authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        AuthenticationResponse response = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request, Authentication authentication) {
        BlogUserDetails userDetails = (BlogUserDetails) authentication.getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthenticationResponse response = authenticationService.registerAndAuthenticate(registerRequest);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
