package com.universalis.blog.domain.user.services.impl;

import com.universalis.blog.domain.user.dtos.RegisterRequest;
import com.universalis.blog.domain.user.dtos.UserDTO;
import com.universalis.blog.domain.user.entities.User;
import com.universalis.blog.domain.user.repositories.UserRepository;
import com.universalis.blog.exceptions.UserRegistrationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .name("Mariusz Pudzianowski")
                .email("mariusz.pudzianowski@example.com")
                .password("very-complicated-password")
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .name("Mariusz Pudzianowski")
                .email("mariusz.pudzianowski@example.com")
                .password("haslomaslo")
                .confirmPassword("haslomaslo")
                .build();
    }

    @Test
    void getUserByIdWithValidIdShouldReturnUser() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        // when
        User result = userService.getUserById(userId);
        // then
        assertEquals(userId, result.getId());
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserByIdWithInvalidIdShouldThrowException() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // when
        Executable result = () -> userService.getUserById(userId);
        // then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, result);
        assertEquals("User not found with id " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void registerUserWithValidDataShouldReturnUserDTO() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(testUser.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        // when
        UserDTO result = userService.registerUser(registerRequest);
        // then
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserWithMismatchedPasswordsShouldThrowException() {
        // given
        registerRequest.setConfirmPassword("different-password");
        // when
        Executable result = () -> userService.registerUser(registerRequest);
        // then
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, result);
        assertEquals("Password and confirmation password do not match", exception.getMessage());
        verify(userRepository, never()).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUserWithExistingEmailShouldThrowException() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        // when
        Executable result = () -> userService.registerUser(registerRequest);
        // then
        UserRegistrationException exception = assertThrows(UserRegistrationException.class, result);
        assertEquals("Email address is already registered", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(registerRequest.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void emailExistsWithExistingEmailShouldReturnTrue() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        // when
        boolean result = userService.emailExists("mariusz.pudzianowski@example.com");
        // then
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("mariusz.pudzianowski@example.com");
    }

    @Test
    void emailExistsWithNonExistingEmailShouldReturnFalse() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // when
        boolean result = userService.emailExists("notexistingemail@example.com");
        // then
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("notexistingemail@example.com");
    }

    @Test
    void emailExistsWithUppercaseEmailShouldReturnTrue() {
        // given
        String inputEmail = "MARIUSZ.PUDZIANOWSKI@example.com";
        String lowerCasedEmail = "mariusz.pudzianowski@example.com";
        when(userRepository.findByEmail(lowerCasedEmail)).thenReturn(Optional.of(testUser));
        // when
        boolean result = userService.emailExists(inputEmail);
        // then
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(lowerCasedEmail);
    }
}