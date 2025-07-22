package com.sokoplace.customerOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    // JPA passes method names and creates custom SQL queries
    Optional<CustomerOrder> findOrdersByCustomerId(Long Id);

    //List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}

// JPA default methods: findById(ID id), etc.
