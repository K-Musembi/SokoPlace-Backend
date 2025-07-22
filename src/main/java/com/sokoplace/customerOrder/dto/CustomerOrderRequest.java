package com.sokoplace.customerOrder.dto;

import com.sokoplace.product.Product;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerOrderRequest(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Order list is required")
        @NotEmpty(message = "Order items cannot be empty")
        List<Product> orderItems
) {}
