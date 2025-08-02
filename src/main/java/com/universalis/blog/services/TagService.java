package com.universalis.blog.services;

import com.universalis.blog.domain.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
    List<Tag> getTags();
    Tag getTagById(UUID id);
    List<Tag> createTags(Set<String> tagNamesToCreate);
    void deleteTag(UUID id);
}
