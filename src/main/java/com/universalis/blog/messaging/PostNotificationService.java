package com.universalis.blog.messaging;

import org.springframework.stereotype.Service;

@Service
public class PostNotificationService {

    private final EmailService emailService;

    public PostNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void notifyAboutNewPost(PostCreatedMessage message) {
        emailService.sendNewPostEmail(message);
    }
}
