package com.sokoplace.order;

import com.sokoplace.customer.Customer;
import com.sokoplace.customer.CustomerRepository;
import com.sokoplace.order.dto.OrderRequest;
import com.sokoplace.order.dto.OrderResponse;
import com.sokoplace.product.Product;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setProducts(orderRequest.orderItems());

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrder(Long Id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setProducts(orderRequest.orderItems());

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse findOrderById(Long Id) {
        Order order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Transactional
    public List<OrderResponse> findOrdersByCustomerId(Long Id) {
        List<Order> orders = orderRepository.findOrdersByCustomerId(Id);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional
    public void deleteOrder(Long Id) {
        Order order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        Double totalPrice = 0.0;
        for (Product product : order.getProducts()) {
            totalPrice += product.getPrice();
        }
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),  // getter and setter methods offered by Lombok in entity class
                order.getProducts(),
                order.getProducts().size(),
                totalPrice
        );
    }
}
