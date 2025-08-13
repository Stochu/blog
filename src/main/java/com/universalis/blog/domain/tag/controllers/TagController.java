package com.universalis.blog.domain.tag.controllers;

import com.universalis.blog.domain.tag.dtos.CreateTagsRequest;
import com.universalis.blog.domain.tag.dtos.TagDTO;
import com.universalis.blog.domain.tag.entities.Tag;
import com.universalis.blog.domain.tag.mappers.TagMapper;
import com.universalis.blog.domain.tag.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<Tag> tags = tagService.getTags();
        List<TagDTO> tagDTO = tags.stream()
                .map(tagMapper::toDTO)
                .toList();
        return ResponseEntity.ok(tagDTO);
    }

    @PostMapping
    public ResponseEntity<List<TagDTO>> createTag(@RequestBody CreateTagsRequest createTagsRequest) {
        List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
        List<TagDTO> createdTagDTO = savedTags.stream()
                .map(tagMapper::toDTO)
                .toList();
        return new ResponseEntity<>(createdTagDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
