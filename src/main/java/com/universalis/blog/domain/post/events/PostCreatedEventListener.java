package com.universalis.blog.domain.post.events;

import com.universalis.blog.domain.post.mappers.PostEventMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

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
