package com.universalis.blog.domain.authentication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universalis.blog.config.SecurityConfig;
import com.universalis.blog.domain.authentication.dtos.AuthenticationResponse;
import com.universalis.blog.domain.authentication.dtos.LoginRequest;
import com.universalis.blog.domain.authentication.dtos.LogoutRequest;
import com.universalis.blog.domain.authentication.dtos.TokenRefreshRequest;
import com.universalis.blog.domain.authentication.services.AuthenticationService;
import com.universalis.blog.domain.authentication.services.impl.RefreshTokenService;
import com.universalis.blog.domain.user.dtos.RegisterRequest;
import com.universalis.blog.domain.user.entities.User;
import com.universalis.blog.security.BlogUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    // Mocks for SecurityConfig
    @MockitoBean
    private org.springframework.security.web.AuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private com.universalis.blog.domain.user.repositories.UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {

        loginRequest = LoginRequest.builder()
                .email("johny.bravo@example.com")
                .password("qwertyui")
                .build();

        registerRequest = RegisterRequest.builder()
                .name("Johny Bravo")
                .email("johny.bravo@example.com")
                .password("qwertyui")
                .confirmPassword("qwertyui")
                .build();

        authResponse = AuthenticationResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test")
                .refreshToken("refresh-token-value")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    @Test
    void loginWithValidCredentialsShouldReturnAuthResponse() throws Exception {
        // given
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(authResponse);
        // when then
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-value"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void loginWithInvalidRequestShouldReturnUnauthorized() throws Exception {
        // given
        loginRequest.setEmail("wrongEmail@example.com");
        loginRequest.setPassword("wrongPassword");
        when(authenticationService.authenticate(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void registerWithValidRequestShouldReturnCreated() throws Exception {
        // given
        when(authenticationService.registerAndAuthenticate(any(RegisterRequest.class))).thenReturn(authResponse);
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void registerWithInvalidDataShouldReturnBadRequest() throws Exception {
        // given
        registerRequest.setEmail("im-not-email-format");
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void refreshTokenWithValidTokenShouldReturnNewTokens() throws Exception {
        // given
        TokenRefreshRequest refreshRequest = TokenRefreshRequest.builder()
                .refreshToken("refresh-token")
                .build();

        when(authenticationService.refreshToken(anyString()))
                .thenReturn(authResponse);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-value"));
    }

    @Test
    @WithMockUser
    void refreshTokenWithInvalidTokenShouldReturnBadRequest() throws Exception {
        // given
        TokenRefreshRequest refreshRequest = TokenRefreshRequest.builder()
                .refreshToken("")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/refresh-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void logout_WithAuthenticatedUser_ShouldReturnOk() throws Exception {
        // given
        BlogUserDetails userDetails = new BlogUserDetails(new User(UUID.randomUUID(), "abc@example.com", "password12", "Johny",
                Collections.emptyList(), LocalDateTime.now()));
        LogoutRequest logoutRequest = LogoutRequest.builder()
                .refreshToken("refresh-token-to-invalidate")
                .build();
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)));
        // then
        result
                .andDo(print())
                .andExpect(status().isOk());
    }
}
