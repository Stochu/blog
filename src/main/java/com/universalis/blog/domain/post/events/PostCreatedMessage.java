package com.universalis.blog.domain.post.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Integration message representing the fact that a blog post
 * has been created.
 *
 * <p>
 * This message is intended to be sent outside the application boundary
 * (e.g. via Kafka) so that other systems or components can react to the
 * post creation event.
 * </p>
 */
public record PostCreatedMessage(UUID postId, String title, UUID authorId, LocalDateTime createdAt) {

}
