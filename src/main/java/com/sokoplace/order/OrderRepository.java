package com.sokoplace.order;

import com.sokoplace.customer.Customer;
import com.sokoplace.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrdersByCustomer(Customer customer);
    List<Order> findByOrdersCustomerId(Long Id);
    List<Order> findOrdersByProductsContaining(Product product);
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByCreatedAtAfter(LocalDateTime startDate);
    List<Order> findByCreatedAtBefore(LocalDateTime endDate);

}
