package com.universalis.blog.domain.post.controllers;

import com.universalis.blog.domain.post.dtos.CreatePostRequest;
import com.universalis.blog.domain.post.dtos.UpdatePostRequest;
import com.universalis.blog.domain.post.dtos.CreatePostRequestDTO;
import com.universalis.blog.domain.post.dtos.PostDTO;
import com.universalis.blog.domain.post.dtos.UpdatePostRequestDTO;
import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.auth.entities.User;
import com.universalis.blog.domain.post.mappers.PostMapper;
import com.universalis.blog.domain.post.services.PostService;
import com.universalis.blog.domain.auth.services.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<PostDTO>> getAllPosts(@RequestParam(required = false) UUID categoryId,
                                                     @RequestParam(required = false) UUID tagId) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostDTO> postDTOs = posts.stream()
                .map(postMapper::toDTO)
                .toList();
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable UUID id) {
        Post post = postService.getPost(id);
        PostDTO postDTO = postMapper.toDTO(post);
        return ResponseEntity.ok(postDTO);
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
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostRequestDTO createPostRequestDTO,
                                              @RequestAttribute UUID userId) {
        User loggedInUser = userService.getUserById(userId);
        CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDTO);
        Post createdPost = postService.createPost(loggedInUser, createPostRequest);
        PostDTO createdPostDTO = postMapper.toDTO(createdPost);
        return new ResponseEntity<>(createdPostDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable UUID id,
                                              @Valid @RequestBody UpdatePostRequestDTO updatePostRequestDTO) {
        UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDTO);
        Post updatedPost = postService.updatePost(id, updatePostRequest);
        PostDTO updatedPostDTO = postMapper.toDTO(updatedPost);
        return ResponseEntity.ok(updatedPostDTO);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
