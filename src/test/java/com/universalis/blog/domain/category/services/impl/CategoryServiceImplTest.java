package com.universalis.blog.domain.category.services.impl;

import com.universalis.blog.domain.category.entities.Category;
import com.universalis.blog.domain.category.repositories.CategoryRepository;
import com.universalis.blog.domain.post.entities.Post;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    private Category testCategory;
    private UUID testCategoryId;

    @BeforeEach
    void setUp() {
        // Generate a unique ID for test category
        testCategoryId = UUID.randomUUID();

        // Create a test category with empty posts list (no associations)
        testCategory = Category.builder()
                .id(testCategoryId)
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
    }

    @Test
    void listCategoriesShouldReturnAllCategories() {
        // given
        Category category2 = Category.builder()
                .id(UUID.randomUUID())
                .name("Science")
                .posts(new ArrayList<>())
                .build();
        List<Category> expectedCategories = List.of(testCategory, category2);
        when(categoryRepository.findAllWithPosts()).thenReturn(expectedCategories);
        // when
        List<Category> result = categoryService.listCategories();
        // then
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return exactly 2 categories");
        assertEquals(expectedCategories, result, "Should return the same list from repository");
        verify(categoryRepository, times(1)).findAllWithPosts();
    }

    @Test
    void listCategoriesShouldReturnEmptyListWhenNoCategoriesExist() {
        // given
        when(categoryRepository.findAllWithPosts()).thenReturn(new ArrayList<>());
        // when
        List<Category> result = categoryService.listCategories();
        // then
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
        verify(categoryRepository, times(1)).findAllWithPosts();
    }

    @Test
    void getCategoryByIdWithValidIdShouldReturnCategory() {
        // given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        // when
        Category result = categoryService.getCategoryById(testCategoryId);
        // then
        assertNotNull(result, "Result should not be null");
        assertEquals(testCategoryId, result.getId(), "Category ID should match");
        assertEquals("Technology", result.getName(), "Category name should match");
        verify(categoryRepository, times(1)).findById(testCategoryId);
    }

    @Test
    void getCategoryByIdWithInvalidIdShouldThrowEntityNotFoundException() {
        // given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.empty());
        // when
        Executable result = () -> categoryService.getCategoryById(testCategoryId);
        // then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                result,
                "Should throw EntityNotFoundException when category not found"
        );
        assertEquals(
                "Category not found with id " + testCategoryId,
                exception.getMessage(),
                "Exception message should include the category ID"
        );
        verify(categoryRepository, times(1)).findById(testCategoryId);
    }

    @Test
    void createCategoryWithValidDataShouldSaveAndReturnCategory() {
        // given
        Category newCategory = Category.builder()
                .name("Technology")
                .posts(new ArrayList<>())
                .build();
        when(categoryRepository.existsByNameIgnoreCase("Technology")).thenReturn(false);
        when(categoryRepository.save(newCategory)).thenReturn(testCategory);
        // when
        Category result = categoryService.createCategory(newCategory);
        // then
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getId(), "Saved category should have an ID");
        assertEquals("Technology", result.getName(), "Category name should match");

        // Verify repository interactions in correct order
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Technology");
        verify(categoryRepository, times(1)).save(newCategory);
    }

    @Test
    void createCategoryWithDuplicateNameShouldThrowIllegalArgumentException() {
        // given
        when(categoryRepository.existsByNameIgnoreCase("Technology")).thenReturn(true);
        // when
        Executable result = () -> categoryService.createCategory(testCategory);
        // then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                result,
                "Should throw IllegalArgumentException when category name already exists"
        );
        assertEquals(
                "Category already exists with name: Technology",
                exception.getMessage(),
                "Exception message should include the duplicate category name"
        );
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Technology");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategoryWithNoPosts() {
        // given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        // when
        categoryService.deleteCategory(testCategoryId);
        // then
        verify(categoryRepository, times(1)).findById(testCategoryId);
        verify(categoryRepository, times(1)).deleteById(testCategoryId);
    }

    @Test
    void deleteCategoryWithPostsShouldThrowIllegalStateException() {
        // given
        Post mockPost = mock(Post.class);
        testCategory.getPosts().add(mockPost);
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.of(testCategory));
        // when
        Executable result = () -> categoryService.deleteCategory(testCategoryId);
        // then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                result,
                "Should throw IllegalStateException when category has associated posts"
        );
        assertEquals(
                "Category has posts associated with it",
                exception.getMessage(),
                "Exception message should indicate post associations prevent deletion"
        );
        verify(categoryRepository, times(1)).findById(testCategoryId);
        verify(categoryRepository, never()).deleteById(testCategoryId);
    }

    @Test
    void deleteCategoryWithNonExistentIdShouldDoNothing() {
        // given
        when(categoryRepository.findById(testCategoryId)).thenReturn(Optional.empty());
        // when
        categoryService.deleteCategory(testCategoryId);
        // then
        verify(categoryRepository, times(1)).findById(testCategoryId);
        verify(categoryRepository, never()).deleteById(any(UUID.class));
    }

}