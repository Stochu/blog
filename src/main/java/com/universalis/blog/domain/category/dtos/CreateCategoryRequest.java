package com.universalis.blog.domain.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between {min} and {max} characters")
    @Pattern(regexp = "^[\\w\\s-]+$", message = "Category name can only contain letters, numbers, spaces and hyphens")
    private String name;

    // Custom setter for Jackson
    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    // That overrides part of @Builder implementation.
    public static class CreateCategoryRequestBuilder {
        public CreateCategoryRequestBuilder name(String name) {
            this.name = (name != null) ? name.trim() : null;
            return this;
        }
    }
}
