package com.sokoplace.product.dto;

public record ProductResponse(

        Long Id,
        String category,
        String brand,
        String model,
        Double price,
        String description,
        String imageUrl
) {}
