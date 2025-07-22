package com.sokoplace.customerOrder.dto;

import com.sokoplace.product.Product;

import java.util.List;

public record CustomerOrderResponse(
        Long Id,
        Long customerId,
        String customerName,
        List<Product> orderItems,
        int totalItems,
        Double totalPrice
) {}
