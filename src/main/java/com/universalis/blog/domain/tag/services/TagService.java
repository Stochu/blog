package com.universalis.blog.domain.tag.services;

import com.universalis.blog.domain.tag.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {
    List<Tag> getTags();
    Tag getTagById(UUID id);
    List<Tag> getTagsByIds(Set<UUID> ids);
    List<Tag> createTags(Set<String> tagNamesToCreate);
    void deleteTag(UUID id);

}
