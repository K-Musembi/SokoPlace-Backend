package com.sokoplace.customerOrder;

import com.sokoplace.customer.Customer;
import com.sokoplace.customer.CustomerRepository;
import com.sokoplace.customerOrder.dto.CustomerOrderRequest;
import com.sokoplace.customerOrder.dto.CustomerOrderResponse;
import com.sokoplace.product.Product;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerOrderService(CustomerOrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerOrderResponse createOrder(CustomerOrderRequest orderRequest) {
        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setProducts(orderRequest.orderItems());

        CustomerOrder savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public CustomerOrderResponse updateOrder(Long Id, CustomerOrderRequest orderRequest) {
        CustomerOrder order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setProducts(orderRequest.orderItems());

        CustomerOrder updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    @Transactional
    public CustomerOrderResponse findOrderById(Long Id) {
        CustomerOrder order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Transactional
    public List<CustomerOrderResponse> findOrdersByCustomerId(Long Id) {
        Optional<CustomerOrder> orders = orderRepository.findOrdersByCustomerId(Id);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional
    public void deleteOrder(Long Id) {
        CustomerOrder order = orderRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    private CustomerOrderResponse mapToOrderResponse(CustomerOrder order) {
        Double totalPrice = 0.0;
        for (Product product : order.getProducts()) {
            totalPrice += product.getPrice();
        }
        return new CustomerOrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),  // getter and setter methods offered by Lombok in entity class
                order.getProducts(),
                order.getProducts().size(),
                totalPrice
        );
    }
}
