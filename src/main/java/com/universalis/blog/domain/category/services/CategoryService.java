package com.universalis.blog.domain.category.services;

import com.universalis.blog.domain.category.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> listCategories();
    Category getCategoryById(UUID id);
    Category createCategory(Category category);
    void deleteCategory(UUID id);
}
