package com.universalis.blog.domain.category.repositories;

import com.universalis.blog.domain.category.entities.Category;
import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.post.entities.PostStatus;
import com.universalis.blog.domain.user.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testAuthor;
    private Category technologyCategory;
    private Category healthCategory;
    private Category emptyCategory;


    @BeforeEach
    void setUp() {
        testAuthor = User.builder()
                .name("Adam Małysz")
                .email("adas.lec@example.com")
                .password("red-bull-encrypted")
                .createdAt(LocalDateTime.now())
                .build();

        technologyCategory = Category.builder()
                .name("Technology")
                .build();

        healthCategory = Category.builder()
                .name("Health")
                .build();

        emptyCategory = Category.builder()
                .name("Empty Category")
                .build();
    }

    @Test
    void findAllWithPostsShouldReturnCategoriesWithPosts() {
        // given
        entityManager.persistAndFlush(testAuthor);
        entityManager.persistAndFlush(technologyCategory);
        entityManager.persistAndFlush(healthCategory);
        entityManager.persistAndFlush(emptyCategory);

        Post techPost1 = createPost("Spring Boot Tutorial", technologyCategory, testAuthor);
        Post techPost2 = createPost("How to build airplane", technologyCategory, testAuthor);
        entityManager.persistAndFlush(techPost1);
        entityManager.persistAndFlush(techPost2);
        Post healthPost = createPost("Ski Jumping instead of gym?", healthCategory, testAuthor);
        entityManager.persistAndFlush(healthPost);
        entityManager.clear();
        // when
        List<Category> categories = categoryRepository.findAllWithPosts();
        // then
        assertThat(categories).hasSize(3);
        // Verify posts are eagerly loaded (accessing the collection shouldn't throw LazyInitializationException)
        Category techCategory = categories.stream()
                .filter(c -> c.getName().equals("Technology"))
                .findFirst()
                .orElseThrow();
        // Verify posts collection is accessible and contains the correct number of posts
        assertThat(techCategory.getPosts()).hasSize(2);
        // Verify health category has one post
        Category healthCat = categories.stream()
                .filter(c -> c.getName().equals("Health"))
                .findFirst()
                .orElseThrow();
        assertThat(healthCat.getPosts()).hasSize(1);
        // Verify empty category has no posts (LEFT JOIN should still include it)
        Category emptyCat = categories.stream()
                .filter(c -> c.getName().equals("Empty Category"))
                .findFirst()
                .orElseThrow();
        assertThat(emptyCat.getPosts()).isEmpty();
    }

    @Test
    void findAllWithPostCountShouldReturnEmptyListWhenNoCategoriesExist() {
        // When
        List<Category> categories = categoryRepository.findAllWithPosts();
        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).isEmpty();
    }

    @Test
    void findAllWithPostsShouldAvoidNPlusOneQueryProblem() {
        entityManager.persistAndFlush(testAuthor);

        for (int i = 0; i < 5; i++) {
            Category category = Category.builder()
                    .name("Category " + i)
                    .build();
            entityManager.persistAndFlush(category);

            // Add 2 posts to each category
            Post post1 = createPost("Post " + i + "-1", category, testAuthor);
            Post post2 = createPost("Post " + i + "-2", category, testAuthor);
            entityManager.persistAndFlush(post1);
            entityManager.persistAndFlush(post2);
        }
        entityManager.clear();
        // when
        List<Category> categories = categoryRepository.findAllWithPosts();
        // then
        assertThat(categories).hasSize(5);
        // All posts should be accessible without additional queries
        // If N+1 problem existed, accessing posts would trigger lazy loading
        categories.forEach(category -> {
            assertThat(category.getPosts()).hasSize(2);
            // Access posts field to ensure they're fully loaded
            category.getPosts().forEach(post -> assertThat(post.getTitle()).isNotNull());
        });
    }

    @Test
    void findAllWithPostsShouldHandleMultiplePostsCorrectly() {
        // given
        entityManager.persistAndFlush(testAuthor);
        entityManager.persistAndFlush(technologyCategory);
        // Create 10 posts for one category
        for (int i = 1; i <= 10; i++) {
            Post post = createPost("Tech Post " + i, technologyCategory, testAuthor);
            entityManager.persistAndFlush(post);
        }
        entityManager.clear();
        // when
        List<Category> categories = categoryRepository.findAllWithPosts();
        // then
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getPosts()).hasSize(10);
    }

    @Test
    void existsByNameIgnoreCaseShouldReturnTrueForExactMatch() {
        // Given
        entityManager.persistAndFlush(technologyCategory);
        entityManager.clear();
        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("Technology");
        // Then
        assertTrue(exists);
    }

    @Test
    void existsByNameIgnoreCaseShouldReturnTrueForDifferentCase() {
        // Given
        entityManager.persistAndFlush(technologyCategory);
        entityManager.clear();
        // When & Then
        assertTrue(categoryRepository.existsByNameIgnoreCase("technology"));
        assertTrue(categoryRepository.existsByNameIgnoreCase("TECHNOLOGY"));
        assertTrue(categoryRepository.existsByNameIgnoreCase("TeCHnoLoGy"));
        assertTrue(categoryRepository.existsByNameIgnoreCase("Technology"));
    }

    @Test
    void existsByNameIgnoreCaseShouldReturnFalseForNonExistentCategory() {
        // Given
        entityManager.clear();
        // When
        boolean exists = categoryRepository.existsByNameIgnoreCase("NonExistent");
        // Then
        assertFalse(exists);
    }

    @Test
    void existsByNameIgnoreCaseShouldHandleNullParameter() {
        // When & Then
        assertFalse(categoryRepository.existsByNameIgnoreCase(null));
    }

    @Test
    void existsByNameIgnoreCaseShouldReturnFalseForEmptyString() {
        // Given
        entityManager.persistAndFlush(technologyCategory);
        entityManager.clear();
        // When & Then
        assertFalse(categoryRepository.existsByNameIgnoreCase(""));
    }

    /**
     * Helper method to create a Post entity with required fields.
     *
     * @param title Post title
     * @param category Associated category
     * @param author Post author
     * @return Constructed Post entity (not persisted)
     */
    private Post createPost(String title, Category category, User author) {
        return Post.builder()
                .title(title)
                .content("Sample content for " + title)
                .status(PostStatus.PUBLISHED)
                .readingTime(5)
                .author(author)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}