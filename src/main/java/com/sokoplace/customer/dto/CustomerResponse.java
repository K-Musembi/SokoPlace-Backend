package com.sokoplace.customer.dto;

// DTO: Data Transfer Object

// DTOs are designed to separate the internal representation of your domain model,
// from the data you expose or access externally through your API.

public record CustomerResponse(
        Long Id,
        String name,
        String email
) {}
