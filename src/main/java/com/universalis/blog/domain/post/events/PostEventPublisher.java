package com.universalis.blog.domain.post.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
