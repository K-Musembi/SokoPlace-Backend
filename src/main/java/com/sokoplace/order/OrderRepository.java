package com.sokoplace.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPA passes method names and creates custom SQL queries
    Optional<Order> findOrdersByCustomerId(Long Id);

    //List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}

// JPA default methods: findById(ID id), etc.
