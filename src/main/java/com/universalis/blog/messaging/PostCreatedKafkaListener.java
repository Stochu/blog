package com.universalis.blog.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PostCreatedKafkaListener {

    private final PostNotificationService postNotificationService;

    public PostCreatedKafkaListener(PostNotificationService postNotificationService) {
        this.postNotificationService = postNotificationService;
    }

    @KafkaListener(topics = "post-created", containerFactory = "kafkaListenerContainerFactory")
    public void handle(PostCreatedMessage message) {
        postNotificationService.notifyAboutNewPost(message);
    }

}
