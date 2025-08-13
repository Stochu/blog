package com.universalis.blog.domain.post.repositories;

import com.universalis.blog.domain.post.entities.PostStatus;
import com.universalis.blog.domain.category.entities.Category;
import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.tag.entities.Tag;
import com.universalis.blog.domain.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag);
    List<Post> findAllByStatusAndCategory(PostStatus status, Category category);
    List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);
    List<Post> findAllByStatus(PostStatus status);
    List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
