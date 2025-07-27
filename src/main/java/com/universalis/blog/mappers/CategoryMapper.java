package com.universalis.blog.mappers;

import com.universalis.blog.domain.PostStatus;
import com.universalis.blog.domain.dtos.CategoryDTO;
import com.universalis.blog.domain.dtos.CreateCategoryRequest;
import com.universalis.blog.domain.entities.Category;
import com.universalis.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryDTO toDTO(Category category);


    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if (posts == null) {
            return 0;
        }
        return posts.stream()
                .filter(post -> post.getStatus()
                        .equals(PostStatus.PUBLISHED)).count();
    }

    Category toEntity(CreateCategoryRequest createCategoryRequest);
}
