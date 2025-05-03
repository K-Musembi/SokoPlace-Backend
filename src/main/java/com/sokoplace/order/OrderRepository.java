package com.sokoplace.order;

import com.sokoplace.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPA passes method names and creates custom SQL queries
    List<Order> findOrdersByCustomer(Customer customer);
    List<Order> findOrdersByCustomerId(Long Id);
    // List<Order> findOrdersByProductsContaining(Product product);
    // List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    // List<Order> findByCreatedAtAfter(LocalDateTime startDate);
    // List<Order> findByCreatedAtBefore(LocalDateTime endDate);
}

// JPA default methods: findById(ID id), etc.
