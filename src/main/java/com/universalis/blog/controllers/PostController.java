package com.universalis.blog.controllers;

import com.universalis.blog.domain.CreatePostRequest;
import com.universalis.blog.domain.dtos.CreatePostRequestDTO;
import com.universalis.blog.domain.dtos.PostDTO;
import com.universalis.blog.domain.entities.Post;
import com.universalis.blog.domain.entities.User;
import com.universalis.blog.mappers.PostMapper;
import com.universalis.blog.services.PostService;
import com.universalis.blog.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDTO> postDTOs = posts.stream()
                .map(postMapper::toDTO)
                .toList();
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostDTO>> getDraftPosts(@RequestAttribute UUID userId) {
        User user = userService.getUserById(userId);
        List<Post> draftPosts = postService.getDraftPosts(user);
        List<PostDTO> draftPostsDTO = draftPosts.stream()
                .map(postMapper::toDTO)
                .toList();
        return ResponseEntity.ok(draftPostsDTO);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @RequestBody CreatePostRequestDTO createPostRequestDTO,
            @RequestAttribute UUID userId) {

        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDTO);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDTO createdPostDTO = postMapper.toDTO(createdPost);

        return new ResponseEntity<>(createdPostDTO, HttpStatus.CREATED);
    }
}
