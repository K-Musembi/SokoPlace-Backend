package com.sokoplace.customer;

import com.sokoplace.customer.dto.CustomerRequest;
import com.sokoplace.customer.dto.CustomerResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;
    private CustomerRequest customerRequest;

    @BeforeEach
    void setup() {
        customerRequest = new CustomerRequest("test", "test@gmail.com");
        customer1 = new Customer(1L, "one", "one@gmail.com", null, null, null);
        customer2 = new Customer(2L, "two", "two@gmail.com", null, null, null);
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        // Given
        given(customerRepository.findAll()).willReturn(Arrays.asList(customer1, customer2));
        // When
        List<CustomerResponse> customers = customerService.findAllCustomers();
        // Then
        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(2);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should display customers by ID when found")
    void displayCustomersById() {
        // Given
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer1));
        // When
        CustomerResponse foundCustomer = customerService.findCustomerById(1L);
        // Then
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.name()).isEqualTo(customer1.getName());
        assertThat(foundCustomer.email()).isEqualTo(customer1.getEmail());
        assertThat(foundCustomer.Id()).isEqualTo(customer1.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return EntityNotFoundException when customer not found")
    void customerNotFound() {
        // Given
        given(customerRepository.findById(anyLong())).willReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> customerService.findCustomerById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found");
        verify(customerRepository).findById(99L);
    }

    @Test
    @DisplayName("Should create and return new customer")
    void createCustomer() {
        // Given
        Customer customerToSave = new Customer();
        customerToSave.setName(customerRequest.name());
        customerToSave.setEmail(customerRequest.email());

        Customer savedCustomer = new Customer(1L, customerRequest.name(), customerRequest.email(), null, LocalDateTime.now(), LocalDateTime.now());

        given(customerRepository.existsByEmail(customerRequest.email())).willReturn(false);
        given(customerRepository.save(any(Customer.class))).willReturn(savedCustomer);

        // When
        CustomerResponse createdCustomer = customerService.createCustomer(customerRequest);

        // Then
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.name()).isEqualTo(customerRequest.name());
        assertThat(createdCustomer.Id()).isEqualTo(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer when found")
    void updateCustomer() {
        // Given
        CustomerRequest updateRequest = new CustomerRequest("updateName", "updateEmail@gmail.com");
        Long customerId = 1L;

        // This is the entity that the save method will return
        Customer updatedEntity = new Customer(customerId, updateRequest.name(), updateRequest.email(), null, null, null);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer1));
        // IMPROVEMENT: Mock the save call to return the updated entity
        given(customerRepository.save(any(Customer.class))).willReturn(updatedEntity);

        // When
        CustomerResponse updatedCustomer = customerService.updateCustomer(customerId, updateRequest);

        // Then
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.name()).isEqualTo(updateRequest.name());
        assertThat(updatedCustomer.email()).isEqualTo(updateRequest.email());
        assertThat(updatedCustomer.Id()).isEqualTo(customerId);

        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer when found")
    void deleteCustomer() {
        // Given
        Long customerId = 1L;
        given(customerRepository.existsById(customerId)).willReturn(true);
        doNothing().when(customerRepository).deleteById(customerId);

        // When
        customerService.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }
}