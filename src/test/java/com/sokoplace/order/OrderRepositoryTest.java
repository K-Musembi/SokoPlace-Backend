package com.sokoplace.order;

import com.sokoplace.customer.Customer;
import com.sokoplace.customer.CustomerRepository;
import com.sokoplace.product.Product;
import com.sokoplace.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Container
    // This container will be started once and shared across all tests that extend this class.
    static final PostgreSQLContainer<?> postgresqlContainer2 = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer2::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer2::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer2::getPassword);
        // Let Hibernate create the schema for our test container
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Disable Flyway for this test slice
        registry.add("spring.flyway.enabled", () -> "false");
        // This property was needed for OrderRepositoryTest, it's safe to have it for all.
        registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "true");
    }

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setup() {
        // Clean up repositories to ensure test isolation
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        // Arrange: A persisted customer is required for creating an order
        customer = new Customer(null, "Test Customer", "customer@test.com", null, null, null);
        testEntityManager.persistAndFlush(customer);

        order1 = new Order(null,  customer, null, null, new ArrayList<>());
        order2 = new Order(null,  customer, null, null, new ArrayList<>());
    }

    @Test
    @DisplayName("Should save an order")
    void shouldSaveOrder() {
        // Act
        Order savedOrder = orderRepository.save(order1);

        // Assert
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull().isPositive();
        assertThat(savedOrder.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(savedOrder.getUpdatedAt()).isNotNull();
        assertThat(savedOrder.getCreatedAt()).isEqualTo(savedOrder.getUpdatedAt());
    }

    @Test
    @DisplayName("Should find an order by Id")
    void shouldFindOrderById() {
        // Arrange
        Order persistedOrder = testEntityManager.persistAndFlush(order1);

        // Act
        Optional<Order> foundOrderOpt = orderRepository.findById(persistedOrder.getId());

        // Assert
        assertThat(foundOrderOpt).hasValueSatisfying(foundOrder -> {
            assertThat(foundOrder.getId()).isEqualTo(persistedOrder.getId());
            assertThat(foundOrder.getCustomer().getId()).isEqualTo(customer.getId());
        });
    }

    @Test
    @DisplayName("Should find all orders")
    void shouldFindAllOrders() {
        // Arrange
        orderRepository.saveAll(List.of(order1, order2));

        // Act
        List<Order> orders = orderRepository.findAll();

        // Assert
        assertThat(orders)
                .hasSize(2)
                .extracting(Order::getCustomer)
                .containsOnly(customer);
    }

    @Test
    @DisplayName("Should update an order by adding a product")
    void shouldUpdateOrder() throws InterruptedException {
        // Arrange
        // Assuming Product entity exists and has a compatible constructor
        Product product = new Product(null, "SK001", "Electronics", "Nokia", "3310", 199.00, "Latest feature phone", "/path/to/image.jpg", new ArrayList<>(), null, null);
        testEntityManager.persist(product);

        Order persistedOrder = testEntityManager.persistAndFlush(order1);
        LocalDateTime initialUpdateTime = persistedOrder.getUpdatedAt();

        // Introduce a small delay to ensure the 'updatedAt' timestamp will be different
        Thread.sleep(10);

        // Act
        persistedOrder.getProducts().add(product);
        Order updatedOrder = orderRepository.saveAndFlush(persistedOrder);

        // Assert
        Order reloadedOrder = testEntityManager.find(Order.class, updatedOrder.getId());
        assertThat(reloadedOrder).isNotNull();
        assertThat(reloadedOrder.getProducts()).hasSize(1);
        assertThat(reloadedOrder.getProducts().get(0).getBrand()).isEqualTo("Nokia");
        assertThat(reloadedOrder.getUpdatedAt()).isAfter(initialUpdateTime);
    }

    @Test
    @DisplayName("Should delete an order")
    void shouldDeleteOrder() {
        // Arrange
        Order persistedOrder = testEntityManager.persistAndFlush(order1);

        // Act
        orderRepository.deleteById(persistedOrder.getId());
        testEntityManager.flush(); // Ensure the delete statement is sent to the DB
        testEntityManager.clear(); // Clear the persistence context to force a DB hit on the next find

        // Assert
        Optional<Order> foundOrder = orderRepository.findById(persistedOrder.getId());
        assertThat(foundOrder).isEmpty();
    }
}