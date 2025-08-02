package com.universalis.blog.controllers;

import com.universalis.blog.domain.dtos.PostDTO;
import com.universalis.blog.domain.entities.Post;
import com.universalis.blog.mappers.PostMapper;
import com.universalis.blog.services.PostService;
import lombok.RequiredArgsConstructor;
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
}
