package com.universalis.blog.domain.category.mappers;

import com.universalis.blog.domain.post.entities.PostStatus;
import com.universalis.blog.domain.category.dtos.CategoryDTO;
import com.universalis.blog.domain.category.dtos.CreateCategoryRequest;
import com.universalis.blog.domain.category.entities.Category;
import com.universalis.blog.domain.post.entities.Post;
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
