package com.sokoplace.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokoplace.order.dto.OrderRequest;
import com.sokoplace.order.dto.OrderResponse;
import com.sokoplace.product.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser // Simulate an authenticated user for all tests (Spring Security)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Use @MockitoBean to mock the service layer in a @WebMvcTest
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequest validOrderRequest;
    private OrderRequest invalidOrderRequest;
    private OrderResponse orderResponse1;
    private OrderResponse orderResponse2;

    @BeforeEach
    void setup() {
        // Assuming a Product class/record with a constructor for test data setup.
        Product product1 = new Product(101L, "SK101", "Electronics", "Samsung", "A15", 299.00, "Latest smartphone", "/path/to/image.jpg", new ArrayList<>(), null, null);
        Product product2 = new Product(102L, "SK102", "Electronics", "Nokia", "3310", 199.00, "Latest feature phone", "/path/to/image.jpg", new ArrayList<>(), null, null);
        List<Product> products = List.of(product1, product2);

        // A valid request to create/update an order
        validOrderRequest = new OrderRequest(1L, products);

        // An invalid request that should be caught by @Valid
        invalidOrderRequest = new OrderRequest(null, Collections.emptyList());

        // Sample responses returned from the mocked service
        orderResponse1 = new OrderResponse(1L, 1L, "Test Customer", products, 2, 1225.50);
        orderResponse2 = new OrderResponse(2L, 2L, "Another Customer", List.of(product1), 1, 1200.50);
    }

    // --- POST /api/v1/orders ---

    @Test
    @DisplayName("POST /api/v1/orders - Should create a new order with valid data")
    void createOrder_withValidRequest_shouldReturnCreated() throws Exception {
        given(orderService.createOrder(any(OrderRequest.class))).willReturn(orderResponse1);

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf()) // Add CSRF token for security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Id").value(orderResponse1.Id()))
                .andExpect(jsonPath("$.customerId").value(orderResponse1.customerId()))
                .andExpect(jsonPath("$.customerName").value(orderResponse1.customerName()))
                .andExpect(jsonPath("$.totalItems").value(orderResponse1.totalItems()))
                .andExpect(jsonPath("$.totalPrice").value(orderResponse1.totalPrice()));

        verify(orderService).createOrder(any(OrderRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/orders - Should return 400 Bad Request with invalid data")
    void createOrder_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        // The @Valid annotation on the controller method should prevent the service from being called.
        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    // --- PUT /api/v1/orders/{id} ---

    @Test
    @DisplayName("PUT /api/v1/orders/{id} - Should update an existing order")
    void updateOrder_whenOrderExists_shouldReturnUpdatedOrder() throws Exception {
        Long orderId = 1L;
        // Note: The controller's update method returns 201 CREATED. While 200 OK is more common for PUT,
        // we test the actual implemented behavior.
        given(orderService.updateOrder(eq(orderId), any(OrderRequest.class))).willReturn(orderResponse1);

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isCreated()) // Asserting based on controller implementation
                .andExpect(jsonPath("$.Id").value(orderResponse1.Id()))
                .andExpect(jsonPath("$.customerName").value(orderResponse1.customerName()));

        verify(orderService).updateOrder(eq(orderId), any(OrderRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/orders/{id} - Should return 404 Not Found if order does not exist")
    void updateOrder_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {
        Long orderId = 99L;
        given(orderService.updateOrder(eq(orderId), any(OrderRequest.class)))
                .willThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrderRequest)))
                .andExpect(status().isNotFound());

        verify(orderService).updateOrder(eq(orderId), any(OrderRequest.class));
    }

    // --- GET /api/v1/orders/{id} ---

    @Test
    @DisplayName("GET /api/v1/orders/{id} - Should return order if found")
    void getOrderById_whenOrderExists_shouldReturnOrder() throws Exception {
        Long orderId = 1L;
        given(orderService.findOrderById(orderId)).willReturn(orderResponse1);

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Id").value(orderResponse1.Id()))
                .andExpect(jsonPath("$.customerName").value(orderResponse1.customerName()));

        verify(orderService).findOrderById(orderId);
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} - Should return 404 Not Found if order does not exist")
    void getOrderById_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {
        Long orderId = 99L;
        given(orderService.findOrderById(orderId)).willThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isNotFound());

        verify(orderService).findOrderById(orderId);
    }

    // --- GET /api/v1/orders/customer/{id} ---

    @Test
    @DisplayName("GET /api/v1/orders/customer/{id} - Should return list of orders for a customer")
    void getOrdersByCustomerId_whenOrdersExist_shouldReturnListOfOrders() throws Exception {
        Long customerId = 1L;
        List<OrderResponse> customerOrders = List.of(orderResponse1, orderResponse2);
        given(orderService.findOrdersByCustomerId(customerId)).willReturn(customerOrders);

        mockMvc.perform(get("/api/v1/orders/customer/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].Id").value(orderResponse1.Id()));

        verify(orderService).findOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("GET /api/v1/orders/customer/{id} - Should return empty list if customer has no orders")
    void getOrdersByCustomerId_whenNoOrdersExist_shouldReturnEmptyList() throws Exception {
        Long customerId = 3L;
        given(orderService.findOrdersByCustomerId(customerId)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders/customer/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));

        verify(orderService).findOrdersByCustomerId(customerId);
    }

    // --- DELETE /api/v1/orders/{id} ---

    @Test
    @DisplayName("DELETE /api/v1/orders/{id} - Should delete an existing order")
    void deleteOrder_whenOrderExists_shouldReturnNoContent() throws Exception {
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(orderId);
    }

    @Test
    @DisplayName("DELETE /api/v1/orders/{id} - Should return 404 Not Found if order does not exist")
    void deleteOrder_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {
        Long orderId = 99L;
        doThrow(new EntityNotFoundException("Order not found")).when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(orderService).deleteOrder(orderId);
    }
}