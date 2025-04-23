package com.sokoplace.order.dto;

import com.sokoplace.customer.Customer;
import com.sokoplace.product.Product;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record OrderRequest(

        @NotBlank(message = "Customer is required")
        Customer customer,

        @NotBlank(message = "Product list is required")
        List<Product> orderItems
) {}
