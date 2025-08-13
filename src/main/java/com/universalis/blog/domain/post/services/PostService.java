package com.universalis.blog.domain.post.services;

import com.universalis.blog.domain.post.dtos.CreatePostRequest;
import com.universalis.blog.domain.post.dtos.UpdatePostRequest;
import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.auth.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<Post> getAllPosts(UUID categoryId, UUID tagId);
    Post getPost(UUID id);
    List<Post> getDraftPosts(User user);
    Post createPost(User user, CreatePostRequest createPostRequest);
    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);
    void deletePost(UUID id);
}
