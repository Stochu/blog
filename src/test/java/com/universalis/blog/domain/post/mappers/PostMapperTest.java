package com.universalis.blog.domain.post.mappers;

import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.post.events.PostCreatedEvent;
import com.universalis.blog.messaging.PostCreatedMessage;
import com.universalis.blog.domain.user.entities.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostEventMapperTest {

    private final PostEventMapper mapper = Mappers.getMapper(PostEventMapper.class);

    @Test
    void shouldMapPostToPostCreatedEvent() {
        // given
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        User author = new User();
        author.setId(authorId);

        Post post = new Post();
        post.setId(postId);
        post.setTitle("Kielecki czy Winiary? Starcie gigantow");
        post.setAuthor(author);
        post.setCreatedAt(createdAt);

        // when
        PostCreatedEvent event = mapper.toPostCreatedEvent(post);

        // then
        assertThat(event).isNotNull();
        assertThat(event.postId()).isEqualTo(postId);
        assertThat(event.title()).isEqualTo("Kielecki czy Winiary? Starcie gigantow");
        assertThat(event.authorId()).isEqualTo(authorId);
        assertThat(event.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldMapPostCreatedEventToPostCreatedMessage() {
        // given
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        PostCreatedEvent postCreatedEvent = PostCreatedEvent
                .builder()
                .postId(postId)
                .title("Kielecki czy Winiary? Starcie gigantow")
                .authorId(authorId)
                .createdAt(createdAt)
                .build();

        // when
        PostCreatedMessage event = mapper.postCreatedEventToMessage(postCreatedEvent);

        // then
        assertThat(event).isNotNull();
        assertThat(event.postId()).isEqualTo(postId);
        assertThat(event.title()).isEqualTo("Kielecki czy Winiary? Starcie gigantow");
        assertThat(event.authorId()).isEqualTo(authorId);
        assertThat(event.createdAt()).isEqualTo(createdAt);
    }
}