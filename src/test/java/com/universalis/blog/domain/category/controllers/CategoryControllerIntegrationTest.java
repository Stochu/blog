package com.universalis.blog.domain.category.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universalis.blog.domain.category.dtos.CategoryDTO;
import com.universalis.blog.domain.category.dtos.CreateCategoryRequest;
import com.universalis.blog.domain.category.entities.Category;
import com.universalis.blog.domain.category.repositories.CategoryRepository;
import com.universalis.blog.domain.post.entities.Post;
import com.universalis.blog.domain.post.entities.PostStatus;
import com.universalis.blog.domain.post.repositories.PostRepository;
import com.universalis.blog.domain.user.entities.User;
import com.universalis.blog.domain.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // to use h2 instead of Postgre
@Transactional // Ensures tests runs in a transaction that's rolled back after completion
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void listCategoriesWhenNoCategoriesExistShouldReturnEmptyList() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void listCategoriesWhenCategoriesExistShouldReturnAllCategories() throws Exception {
        // given
        Category category1 = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        Category category2 = Category.builder()
                .name("Lifestyle")
                .posts(new ArrayList<>())
                .build();
        Category category3 = Category.builder()
                .name("Travel")
                .posts(new ArrayList<>())
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Technology", "Lifestyle", "Travel")))
                .andExpect(jsonPath("$[*].id", everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].postCount", everyItem(is(0))));
    }

    @Test
    @WithMockUser
    void listCategoriesWithPostsShouldReturnCorrectPostCounts() throws Exception {
        // given
        Category category = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        Category savedCategory = categoryRepository.save(category);

        // Create a user (required for post creation)
        User author = User.builder()
                .email("author@example.com")
                .password("password123")
                .name("testauthor")
                .createdAt(LocalDateTime.now())
                .build();
        User savedAuthor = userRepository.save(author);

        // Create posts associated with the category
        Post post1 = Post.builder()
                .title("First Tech Post")
                .content("Content 1")
                .author(savedAuthor)
                .category(savedCategory)
                .tags(new HashSet<>())
                .status(PostStatus.PUBLISHED)
                .readingTime(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        category.getPosts().add(post1);

        Post post2 = Post.builder()
                .title("Second Tech Post")
                .content("Content 2")
                .author(savedAuthor)
                .category(savedCategory)
                .tags(new HashSet<>())
                .status(PostStatus.PUBLISHED)
                .readingTime(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        category.getPosts().add(post2);

        postRepository.save(post1);
        postRepository.save(post2);


        // Create a category without posts
        Category emptyCategory = Category.builder()
                .name("Empty Category")
                .posts(new ArrayList<>())
                .build();
        categoryRepository.save(emptyCategory);
        // when
        System.out.println(post1.getCategory());
        System.out.println(savedCategory.getPosts());
        ResultActions result = mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Technology')].postCount", contains(2)))
                .andExpect(jsonPath("$[?(@.name == 'Empty Category')].postCount", contains(0)));
    }

    @Test
    @WithMockUser
    void createCategoryWithValidDataShouldReturnCreatedCategory() throws Exception {
        // given
        CreateCategoryRequest categoryRequest = CreateCategoryRequest.builder()
                .name("Science")
                .build();

        // when
        ResultActions resultAction = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andDo(print());
        // then
        MvcResult result = resultAction
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Science")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.postCount", is(0)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        objectMapper.readValue(responseBody, CategoryDTO.class);
        assertThat(categoryRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser
    void createCategoryWithBlankNameShouldReturnBadRequest() throws Exception {
        // given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("   ")
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions.andExpect(status().isBadRequest());
        assertThat(categoryRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    void createCategoryWithNullNameShouldReturnBadRequest() throws Exception {
        // given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(null)
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions.andExpect(status().isBadRequest());
        assertThat(categoryRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    void createCategoryWithTooLongNameShouldReturnBadRequest() throws Exception {
        // given
        String longName = "a".repeat(101);
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(longName)
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions.andExpect(status().isBadRequest());
        assertThat(categoryRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    void createCategoryWithDuplicateNameShouldReturnConflict() throws Exception {
        // given
        Category existingCategory = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        categoryRepository.save(existingCategory);

        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Technology")
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest());

        assertThat(categoryRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser
    void createCategoryWithDuplicateIgnoreCasesNameShouldReturnConflict() throws Exception {
        // given
        Category existingCategory = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        categoryRepository.save(existingCategory);
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("TECHNOLOGY")
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isBadRequest());

        assertThat(categoryRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser
    void createCategoryWithNameHavingSpacesShouldTrimAndCreate() throws Exception {
        // given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("  Science  ")
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Science")));
    }


    @Test
    @WithMockUser
    void deleteCategoryWithoutPostsShouldDeleteSuccessfully() throws Exception {
        // given
        Category category = Category.builder()
                .name("ToBeDeleted")
                .posts(new ArrayList<>())
                .build();
        Category savedCategory = categoryRepository.save(category);
        UUID categoryId = savedCategory.getId();
        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(categoryRepository.findById(categoryId)).isEmpty();
        assertThat(categoryRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser
    void deleteCategoryWithNonExistentIdShouldReturnNoContent() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        // When
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/categories/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
                resultActions.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteCategoryWithAssociatedPostsShouldReturnConflict() throws Exception {
        // given
        Category category = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        Category savedCategory = categoryRepository.save(category);

        User author = User.builder()
                .email("author@example.com")
                .password("password123")
                .name("testauthor")
                .createdAt(LocalDateTime.now())
                .build();
        User savedAuthor = userRepository.save(author);

        Post post = Post.builder()
                .title("Tech Article")
                .content("Content")
                .status(PostStatus.PUBLISHED)
                .author(savedAuthor)
                .category(savedCategory)
                .readingTime(10)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        postRepository.save(post);
        savedCategory.getPosts().add(post);
        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/categories/{id}", savedCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        resultActions.andExpect(status().isConflict());
        assertThat(categoryRepository.findById(savedCategory.getId())).isPresent();
        assertThat(categoryRepository.count()).isEqualTo(1);
    }

    @Test
    void listCategories_WithoutAuthentication_ShouldReturnOk() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void createCategoryWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        // given
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Test")
                .build();
        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCategoryWithoutAuthenticationShouldReturnUnauthorized() throws Exception {
        // given
        UUID categoryId = UUID.randomUUID();

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        // then
        resultActions.andExpect(status().isUnauthorized());
    }

}