package com.universalis.blog.controllers;

import com.universalis.blog.domain.dtos.CategoryDTO;
import com.universalis.blog.domain.dtos.CreateCategoryRequest;
import com.universalis.blog.domain.entities.Category;
import com.universalis.blog.mappers.CategoryMapper;
import com.universalis.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> listCategories() {
        List<CategoryDTO> categories = categoryService.listCategories().stream()
                .map(categoryMapper::toDTO)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        Category categoryToCreate = categoryMapper.toEntity(createCategoryRequest);
        Category savedCategory = categoryService.createCategory(categoryToCreate);
        return new ResponseEntity<>(categoryMapper.toDTO(savedCategory), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

