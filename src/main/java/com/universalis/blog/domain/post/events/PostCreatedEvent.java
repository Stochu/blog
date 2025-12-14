package com.universalis.blog.domain.post.events;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published inside the application when a blog post
 * has been successfully created and persisted.
 *
 * <p>
 * This event represents a <strong>fact</strong> in the domain and is used
 * for internal coordination only (e.g. reacting after a transaction commits).
 * It is published via Spring's {@link org.springframework.context.ApplicationEventPublisher}
 * and never leaves the application boundary.
 * </p>
 */
@Builder
public record PostCreatedEvent(UUID postId, String title, UUID authorId, LocalDateTime createdAt) {

}
