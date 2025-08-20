package com.universalis.blog.domain.user.repositories;

import com.universalis.blog.domain.user.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Mariusz Pudzianowski")
                .email("mariusz.pudzianowski@example.com")
                .password("very-complicated-password")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByEmailWithExistingEmailShouldReturnUser() {
        // given
        entityManager.persistAndFlush(testUser);
        entityManager.clear();
        // when
        Optional<User> foundEmail = userRepository.findByEmail("mariusz.pudzianowski@example.com");
        // then
        assertTrue(foundEmail.isPresent());
        assertEquals("mariusz.pudzianowski@example.com", foundEmail.get().getEmail());
    }

    @Test
    void findByEmailWithNonExistentEmailShouldReturnEmpty() {
        // when
        Optional<User> foundEmail = userRepository.findByEmail("idontexist@example.com");
        // then
        assertTrue(foundEmail.isEmpty());
    }
}