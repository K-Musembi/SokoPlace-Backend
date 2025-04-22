package com.sokoplace.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Category is required")
        @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
        String category,

        @NotBlank(message = "Brand is required")
        @Size(min = 2, max = 50, message = "Brand name must be between 2 and 50 characters")
        String brand,

        @NotBlank(message = "Model is required")
        @Size(min = 2, max = 50, message = "Model name must be between 2 and 50 characters")
        String model,

        @NotBlank(message = "Price is required")
        @Min(value = 0, message = "Price must be a positive number")
        Double price,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description
) {}
