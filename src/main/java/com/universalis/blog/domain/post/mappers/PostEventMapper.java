package com.universalis.blog.domain.post.mappers;

import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.post.events.PostCreatedEvent;
import com.universalis.blog.messaging.PostCreatedMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostEventMapper {

    PostCreatedMessage postCreatedEventToMessage(PostCreatedEvent event);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "authorId", source = "author.id")
    PostCreatedEvent toPostCreatedEvent(Post post);


}

