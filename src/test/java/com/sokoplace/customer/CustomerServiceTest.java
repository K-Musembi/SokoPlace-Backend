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

// @Mock: create mock instance of class
// @InjectMocks: create instance of CustomerService and inject mocks
// JUnit, Mockito and AssertJ are all part of 'spring-boot-starter-test'

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;
    private CustomerRequest customerRequest;
    // private CustomerResponse customerResponse;

    @BeforeEach
    void setup() {
        customerRequest = new CustomerRequest("test", "test@gmail.com", "Testpw1");
        customer1 = new Customer(1L, "one", "one@gmail.com", "Onepw1", null, null);
        customer2 = new Customer(2L, "two", "two@gmail.com", "Twopw2", null, null);

        // customerResponse = new CustomerResponse(customer1.getId(), customer1.getName(), customer1.getEmail());
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
        // Mock repository save operation with ID assigned
        Customer savedCustomer = new Customer(1L, customerRequest.name(), customerRequest.email(), customerRequest.password(), LocalDateTime.now(), LocalDateTime.now());
        // Given
        given(customerRepository.save(any(Customer.class))).willReturn(savedCustomer);
        // Then
        CustomerResponse createdCustomer = customerService.createCustomer(customerRequest);

        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.name()).isEqualTo(customerRequest.name());
        assertThat(createdCustomer.Id()).isEqualTo(savedCustomer.getId());
        verify(customerRepository).save(argThat(c -> c.getName().equals(customerRequest.name())));
    }

    @Test
    @DisplayName("Should update customer when found")
    void updateCustomer() {
        // Create Instance of request dto
        CustomerRequest updateRequest = new CustomerRequest("updateName", "updateEmail", "Updatepw1");
        // Customer id of customer to update
        Long customerId = 1L;

        // Given
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer1));
        // When
        CustomerResponse updatedCustomer = customerService.updateCustomer(customerId, updateRequest);
        // Then
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.name()).isEqualTo(updateRequest.name());
        assertThat(updatedCustomer.email()).isEqualTo(updateRequest.email());

        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(argThat(c ->
                c.getId().equals(customerId) &&
                c.getName().equals(updateRequest.name())));
    }

    @Test
    @DisplayName("Should delete customer when found")
    void deleteCustomer() {
        Long customerId = 1L;
        // Given
        given(customerRepository.existsById(customerId)).willReturn(true);
        // Mock delete operation
        doNothing().when(customerRepository).deleteById(customerId);

        customerService.deleteCustomer(customerId);
        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
