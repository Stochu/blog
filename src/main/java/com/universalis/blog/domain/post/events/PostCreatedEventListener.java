package com.universalis.blog.domain.post.events;

import com.universalis.blog.domain.post.mappers.PostEventMapper;
import com.universalis.blog.messaging.PostCreatedMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener that bridges internal domain events to external integration events.
 *
 * <p>
 * This class listens for {@link PostCreatedEvent}, which is a domain-level
 * event published after a blog post is successfully persisted.
 * </p>
 *
 * <p>
 * The listener handler method is annotated with {@link org.springframework.transaction.event.TransactionalEventListener},
 * to ensure that the handler method is invoked <strong>only after</strong>
 * the surrounding database transaction has been successfully committed.
 * </p>
 *
 * <p>
 * This guarantees that:
 * <ul>
 *   <li>No Kafka message is published if the transaction rolls back</li>
 *   <li>External systems observe only facts that are already committed</li>
 *   <li>Infrastructure side effects (Kafka, email, etc.) are decoupled from
 *       core business logic</li>
 * </ul>
 * </p>
 *
 * <p>
 * Inside the listener, the internal {@link PostCreatedEvent} is translated into
 * an external {@link PostCreatedMessage}, which represents an integration message
 * intended for Kafka and other asynchronous consumers.
 * </p>
 *
 * <p>
 * This design keeps:
 * <ul>
 *   <li>the domain layer independent of Kafka</li>
 *   <li>transaction management isolated from messaging concerns</li>
 *   <li>the system extensible for future integrations</li>
 * </ul>
 * </p>
 */
@Component
public class PostCreatedEventListener {

    private PostEventPublisher postEventPublisher;
    private PostEventMapper mapper;

    public PostCreatedEventListener(PostEventPublisher postEventPublisher, PostEventMapper mapper) {
        this.postEventPublisher = postEventPublisher;
        this.mapper = mapper;
    }

    @TransactionalEventListener
    public void handle(PostCreatedEvent event) {
        PostCreatedMessage message = mapper.postCreatedEventToMessage(event);
        postEventPublisher.publishNewPost(message);
    }
}
