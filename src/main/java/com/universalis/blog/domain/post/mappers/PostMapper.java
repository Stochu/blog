package com.universalis.blog.domain.post.mappers;

import com.universalis.blog.domain.post.dtos.CreatePostRequest;
import com.universalis.blog.domain.post.dtos.UpdatePostRequest;
import com.universalis.blog.domain.post.dtos.CreatePostRequestDTO;
import com.universalis.blog.domain.post.dtos.PostDTO;
import com.universalis.blog.domain.post.dtos.UpdatePostRequestDTO;
import com.universalis.blog.domain.post.entities.Post;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostDTO toDTO(Post post);

    CreatePostRequest toCreatePostRequest(@Valid CreatePostRequestDTO dto);
    UpdatePostRequest toUpdatePostRequest(@Valid UpdatePostRequestDTO dto);
}
