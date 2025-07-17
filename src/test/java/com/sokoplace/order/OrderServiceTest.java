package com.sokoplace.order;

import com.sokoplace.customer.Customer;
import com.sokoplace.customer.CustomerRepository;
import com.sokoplace.order.dto.OrderRequest;
import com.sokoplace.order.dto.OrderResponse;
import com.sokoplace.product.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private OrderRequest orderRequest;
    private Customer customer;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setup() {
        // Set up a customer for the tests
        customer = new Customer(1L, "Test Customer", "test@customer.com", null, LocalDateTime.now(), LocalDateTime.now());

        // Setup products to be included in orders
        product1 = new Product(101L, "SK101", "Electronics", "Samsung", "A15", 299.00, "Latest smartphone", "/path/to/image.jpg", new ArrayList<>(), null, null);
        product2 = new Product(102L, "SK102", "Electronics", "Nokia", "3310", 199.00, "Latest feature phone", "/path/to/image.jpg", new ArrayList<>(), null, null);

        // Set up an order request DTO for create/update operations
        orderRequest = new OrderRequest(customer.getId(), Arrays.asList(product1, product2));

        // Set up a complete order entity for retrieval tests
        order1 = new Order(1L, LocalDateTime.now(), LocalDateTime.now(), customer, Arrays.asList(product1, product2));
    }

    @Test
    @DisplayName("Should create and return new order")
    void shouldCreateOrder() {
        // Given
        given(customerRepository.findById(customer.getId())).willReturn(Optional.of(customer));
        given(orderRepository.save(any(Order.class))).willReturn(order1);

        // When
        OrderResponse createdOrder = orderService.createOrder(orderRequest);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.Id()).isEqualTo(order1.getId());
        assertThat(createdOrder.customerId()).isEqualTo(customer.getId());
        assertThat(createdOrder.customerName()).isEqualTo(customer.getName());
        assertThat(createdOrder.totalItems()).isEqualTo(2);
        assertThat(createdOrder.totalPrice()).isEqualTo(1275.50);
        assertThat(createdOrder.orderItems()).hasSize(2);

        verify(customerRepository).findById(customer.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when creating order for non-existent customer")
    void shouldThrowExceptionWhenCustomerNotFoundOnCreate() {
        // Given
        given(customerRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(orderRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found");

        verify(customerRepository).findById(orderRequest.customerId());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should find and return order by ID")
    void shouldFindOrderById() {
        // Given
        given(orderRepository.findById(order1.getId())).willReturn(Optional.of(order1));

        // When
        OrderResponse foundOrder = orderService.findOrderById(order1.getId());

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.Id()).isEqualTo(order1.getId());
        assertThat(foundOrder.customerName()).isEqualTo(customer.getName());
        assertThat(foundOrder.totalItems()).isEqualTo(2);
        assertThat(foundOrder.totalPrice()).isEqualTo(1275.50);

        verify(orderRepository).findById(order1.getId());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when order ID not found")
    void shouldThrowExceptionWhenOrderIdNotFound() {
        // Given
        long nonExistentId = 99L;
        given(orderRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.findOrderById(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should update an existing order")
    void shouldUpdateOrder() {
        // Given
        Long orderId = order1.getId();
        Product newProduct = new Product(103L, "SK103", "Electronics", "Oppo", "2350", 149.00, "Good smartphone", "/path/to/oppo.img", new ArrayList<>(), null, null);
        OrderRequest updateRequest = new OrderRequest(customer.getId(), List.of(newProduct));

        Order updatedOrderEntity = new Order();
        updatedOrderEntity.setId(orderId);
        updatedOrderEntity.setCustomer(customer);
        updatedOrderEntity.setProducts(List.of(newProduct));

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order1));
        given(orderRepository.save(any(Order.class))).willReturn(updatedOrderEntity);

        // When
        OrderResponse updatedOrderResponse = orderService.updateOrder(orderId, updateRequest);

        // Then
        assertThat(updatedOrderResponse).isNotNull();
        assertThat(updatedOrderResponse.Id()).isEqualTo(orderId);
        assertThat(updatedOrderResponse.totalItems()).isEqualTo(1);
        assertThat(updatedOrderResponse.orderItems().get(0).getBrand()).isEqualTo("Oppo");
        assertThat(updatedOrderResponse.totalPrice()).isEqualTo(150.0);

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent order")
    void shouldThrowExceptionWhenUpdatingNonExistentOrder() {
        // Given
        Long nonExistentId = 99L;
        given(orderRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.updateOrder(nonExistentId, orderRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(nonExistentId);
        verify(orderRepository, never()).save(any(Order.class));
    }


    @Test
    @DisplayName("Should find orders by customer ID")
    void shouldFindOrdersByCustomerId() {
        // NOTE: The service's use of `Optional<Order>` from `findOrdersByCustomerId` suggests a customer
        // can only have one order returned, which is unusual. This test validates the current behavior.
        // A more robust implementation might return `List<Order>`.

        // Given
        Long customerId = customer.getId();
        given(orderRepository.findOrdersByCustomerId(customerId)).willReturn(Optional.of(order1));

        // When
        List<OrderResponse> orders = orderService.findOrdersByCustomerId(customerId);

        // Then
        assertThat(orders).isNotNull().hasSize(1);
        assertThat(orders.get(0).Id()).isEqualTo(order1.getId());

        verify(orderRepository).findOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return empty list when no orders found for customer ID")
    void shouldReturnEmptyListForCustomerWithNoOrders() {
        // Given
        Long customerId = customer.getId();
        given(orderRepository.findOrdersByCustomerId(customerId)).willReturn(Optional.empty());

        // When
        List<OrderResponse> orders = orderService.findOrdersByCustomerId(customerId);

        // Then
        assertThat(orders).isNotNull().isEmpty();

        verify(orderRepository).findOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should delete order when found")
    void shouldDeleteOrder() {
        // Given
        Long orderId = order1.getId();
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order1));
        doNothing().when(orderRepository).delete(order1);

        // When
        orderService.deleteOrder(orderId);

        // Then
        verify(orderRepository).findById(orderId);
        verify(orderRepository).delete(order1);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent order")
    void shouldThrowExceptionWhenDeletingNonExistentOrder() {
        // Given
        Long nonExistentId = 99L;
        given(orderRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.deleteOrder(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(nonExistentId);
        verify(orderRepository, never()).delete(any(Order.class));
    }
}