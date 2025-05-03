package com.sokoplace.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private final CustomerRepository customerRepository;
    private Customer customer1;
    private Customer customer2;

    public CustomerRepositoryTest(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup() {
        customer1 = new Customer(1L, "test1", "test@gmail.com", null, null, null);
        customer2 = new Customer(2L, "test2", "test@gmail.com", null, null, null);
    }

    @Test
    @DisplayName("Should save a customer")
    void saveCustomer() {
        Customer savedCustomer = customerRepository.save(customer1);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull().isPositive();
        assertThat(savedCustomer.getName()).isEqualTo(customer1.getName());
    }

    @Test
    @DisplayName("Should find a customer by Id")
    void findCustomerById() {
        testEntityManager.persist(customer1);
        testEntityManager.flush();

        Optional<Customer> foundCustomer = customerRepository.findById(customer1.getId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getId()).isEqualTo(customer1.getId());
        assertThat(foundCustomer.get().getName()).isEqualTo(customer1.getName());
    }

    @Test
    @DisplayName("Should find a customer by email")
    void findCustomerByEmail() {
        testEntityManager.persist(customer1);
        testEntityManager.flush();

        Optional<Customer> foundCustomer = customerRepository.findByEmail(customer1.getEmail());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getId()).isEqualTo(customer1.getId());
        assertThat(foundCustomer.get().getName()).isEqualTo(customer1.getName());
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent Id")
    void emptyOptional() {
        Optional<Customer> foundCustomer = customerRepository.findById(99L);

        assertThat(foundCustomer).isEmpty();
    }

    @Test
    @DisplayName("Should find all customers")
    void findAllCustomers() {
        testEntityManager.persist(customer1);
        testEntityManager.persist(customer2);
        testEntityManager.flush();

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(2);
    }
}
