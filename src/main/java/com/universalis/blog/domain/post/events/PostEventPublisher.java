package com.universalis.blog.domain.post.events;

import com.universalis.blog.messaging.PostCreatedMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publishes post-related integration messages to Kafka.
 *
 * <p>
 * This class is part of the <b>integration layer</b> and is responsible for
 * translating internal domain facts into external messages sent to Kafka.
 * It deliberately contains <b>no business logic</b>.
 * </p>
 *
 * <h2>Design principles</h2>
 * <ul>
 *   <li>
 *     <b>Separation of concerns</b> – domain services publish internal
 *     {@code PostCreatedEvent} events, while this class handles only
 *     the technical responsibility of sending messages to Kafka.
 *   </li>
 *   <li>
 *     <b>Technology isolation</b> – Kafka is kept out of domain services.
 *     If the messaging technology changes in the future, only this class
 *     (and related configuration) should be affected.
 *   </li>
 *   <li>
 *     <b>Event-driven architecture</b> – messages represent facts that
 *     already happened (e.g. a post was created), not intentions.
 *   </li>
 * </ul>
 *
 * <h2>Kafka details</h2>
 * <ul>
 *   <li>The Kafka message key is the post ID ({@link UUID}) to ensure stable
 *       partitioning and ordering per post.</li>
 *   <li>The message value is {@link PostCreatedMessage}, serialized as JSON.</li>
 * </ul>
 */
@Component
public class PostEventPublisher {

    private final static String NEW_POST_TOPIC = "new-posts";

    KafkaTemplate<UUID, PostCreatedMessage> kafkaTemplate;

    public PostEventPublisher(KafkaTemplate<UUID, PostCreatedMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishNewPost(PostCreatedMessage event) {

    }
}
