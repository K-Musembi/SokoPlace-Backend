package com.sokoplace.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTOs are designed to separate the internal representation of your domain model,
// from the data you expose or access externally through your API.

// Record classes are immutable. You can't define instance fields
// Define the fields in the record header, i.e. not inside curly braces
// Records generate fields, constructors, getters, setters, etc.
public record CustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email

        /*@NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
        String password*/
) {}

// Other common constraints: @NotNull, @Min, @Max, etc.
