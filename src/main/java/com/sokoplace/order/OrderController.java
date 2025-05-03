package com.sokoplace.order;

import com.sokoplace.order.dto.OrderRequest;
import com.sokoplace.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")  // It's best practice to version your API
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse responseObject = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);  // HTTP 201 -> created successfully
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse responseObject = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse responseObject = orderService.findOrderById(id);
        return ResponseEntity.ok(responseObject);  // HTTP 200 -> ok
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderByCustomerId(
            @RequestParam Long id) {
        List<OrderResponse> responseObject = orderService.findOrdersByCustomerId(id);
        return ResponseEntity.ok(responseObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();  // HTTP 204 -> no content
    }
}
