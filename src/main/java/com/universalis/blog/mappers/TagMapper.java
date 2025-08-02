package com.universalis.blog.mappers;

import com.universalis.blog.domain.PostStatus;
import com.universalis.blog.domain.dtos.TagDTO;
import com.universalis.blog.domain.entities.Post;
import com.universalis.blog.domain.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    TagDTO toDTO(Tag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts) {
        if (posts == null) {
            return 0;
        }
        return (int) posts
                .stream()
                .filter(post -> post.getStatus().equals(PostStatus.PUBLISHED))
                .count();
    }
}
