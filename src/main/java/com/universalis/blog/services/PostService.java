package com.universalis.blog.services;

import com.universalis.blog.domain.CreatePostRequest;
import com.universalis.blog.domain.entities.Post;
import com.universalis.blog.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    List<Post> getDraftPosts(User user);
    Post createPost(User user, CreatePostRequest createPostRequest);
}
