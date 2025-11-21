package com.universalis.blog.domain.category.repositories;

import com.universalis.blog.domain.category.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // This query fixes n + 1 problem. Solution: in one query all category posts are fetched.
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.posts")
    List<Category> findAllWithPosts();

    boolean existsByNameIgnoreCase(String name);
}
