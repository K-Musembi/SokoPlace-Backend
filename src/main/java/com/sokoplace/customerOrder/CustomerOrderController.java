package com.sokoplace.customerOrder;

import com.sokoplace.customerOrder.dto.CustomerOrderRequest;
import com.sokoplace.customerOrder.dto.CustomerOrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")  // It's best practice to version your API
public class CustomerOrderController {
    private final CustomerOrderService orderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CustomerOrderResponse> createOrder(@Valid @RequestBody CustomerOrderRequest orderRequest) {
        CustomerOrderResponse responseObject = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);  // HTTP 201 -> created successfully
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CustomerOrderRequest orderRequest) {
        CustomerOrderResponse responseObject = orderService.updateOrder(id, orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponse> getOrder(@PathVariable Long id) {
        CustomerOrderResponse responseObject = orderService.findOrderById(id);
        return ResponseEntity.ok(responseObject);  // HTTP 200 -> ok
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<CustomerOrderResponse>> getOrderByCustomerId(
            @PathVariable Long id) {
        List<CustomerOrderResponse> responseObject = orderService.findOrdersByCustomerId(id);
        return ResponseEntity.ok(responseObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerOrderResponse> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();  // HTTP 204 -> no content
    }
}
