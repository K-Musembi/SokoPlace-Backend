package com.sokoplace.order.dto;

import com.sokoplace.product.Product;

import java.util.List;

public record OrderResponse(
        Long Id,
        Long customerId,
        String customerName,
        List<Product> orderItems,
        int totalItems,
        Double totalPrice
) {}
