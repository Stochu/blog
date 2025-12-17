package com.universalis.blog.messaging;

import com.universalis.blog.domain.user.services.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserService userService;

    public EmailService(JavaMailSender mailSender, UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    public void sendNewPostEmail(PostCreatedMessage message) {
        for (String userMail : userService.getAllUsersEmail()) {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(userMail);
            email.setSubject("New blog post: " + message.title());
            email.setText("A new post has been published.\n\n" + "Title: " + message.title()
            );
            mailSender.send(email);
        }

    }
}
