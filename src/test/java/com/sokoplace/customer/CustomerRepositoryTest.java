package com.sokoplace.customer;

import com.sokoplace.test.DatabaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTest extends DatabaseIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setup() {
        // Clean up the repository before each test to ensure test isolation
        customerRepository.deleteAll();
        customer1 = new Customer(null, "test1", "test1@gmail.com", null, null, null);
        customer2 = new Customer(null, "test2", "test2@gmail.com", null, null, null);
    }

    @Test
    @DisplayName("Should save a customer")
    void saveCustomer() {
        // Act
        Customer savedCustomer = customerRepository.save(customer1);

        // Assert
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull().isPositive();
        assertThat(savedCustomer.getName()).isEqualTo(customer1.getName());
    }

    @Test
    @DisplayName("Should find a customer by Id")
    void findCustomerById() {
        // Arrange
        Customer persistedCustomer = testEntityManager.persistAndFlush(customer1);

        // Act
        Optional<Customer> foundCustomerOpt = customerRepository.findById(persistedCustomer.getId());

        // Assert - IMPROVEMENT: Using hasValueSatisfying for a more fluent and safe assertion.
        assertThat(foundCustomerOpt).hasValueSatisfying(foundCustomer -> {
            assertThat(foundCustomer.getId()).isEqualTo(persistedCustomer.getId());
            assertThat(foundCustomer.getName()).isEqualTo(persistedCustomer.getName());
        });
    }

    @Test
    @DisplayName("Should find a customer by email")
    void findCustomerByEmail() {
        // Arrange
        Customer persistedCustomer = testEntityManager.persistAndFlush(customer1);

        // Act
        Optional<Customer> foundCustomerOpt = customerRepository.findByEmail(persistedCustomer.getEmail());

        // Assert
        assertThat(foundCustomerOpt).hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(persistedCustomer.getId());
            assertThat(c.getName()).isEqualTo(persistedCustomer.getName());
        });
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent Id")
    void findById_whenIdDoesNotExist_shouldReturnEmpty() {
        // Act
        Optional<Customer> foundCustomer = customerRepository.findById(999L);

        // Assert
        assertThat(foundCustomer).isEmpty();
    }

    @Test
    @DisplayName("Should find all customers")
    void findAllCustomers() {
        // Arrange
        customerRepository.saveAll(List.of(customer1, customer2));

        // Act
        List<Customer> customers = customerRepository.findAll();

        // Assert - IMPROVEMENT: This assertion is more robust as it doesn't rely on the Customer's equals() method.
        assertThat(customers)
                .hasSize(2)
                .extracting(Customer::getName)
                .containsExactlyInAnyOrder("test1", "test2");
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void existsByEmail() {
        // Arrange
        customerRepository.save(customer1);

        // Act & Assert for existing email
        boolean exists = customerRepository.existsByEmail("test1@gmail.com");
        assertThat(exists).isTrue();

        // Act & Assert for non-existing email
        boolean doesNotExist = customerRepository.existsByEmail("nonexistent@gmail.com");
        assertThat(doesNotExist).isFalse();
    }
}