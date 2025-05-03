package com.sokoplace.customer;

import com.sokoplace.customer.dto.CustomerRequest;
import com.sokoplace.customer.dto.CustomerResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//  Though you could inject repositories directly into controllers,
//  it is best practice to have a service layer.

// Service layer incorporates business logic and coordinates
//  interactions with more than one repository.
// @Service: Marks interface as a Spring Bean (good practice)
// @Autowired: Constructor injection. Use for constructors and setters
// mapCustomerToCustomerResponse(): Helper method to map Entity -> CustomerResponse
// Stream<>: accepts elements from List, etc. but doesn't store. For complex operations
// Stream operations: map, filter, reduce, find, sort, collect
// findCustomerBy id or email in repository returns an Optional object (in case object not found)
// use Optional methods: orElseThrow(), orElse(), ifPresent(), etc.

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        if (customerRepository.existsByEmail(customerRequest.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Customer customer = new Customer();
        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());  // setter method

        customerRepository.save(customer);
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long Id, CustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(Id)
                        .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());

        customerRepository.save(customer);
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse findCustomerById(Long Id) {
        Customer customer = customerRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse findCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return mapToCustomerResponse(customer);
    }

    @Transactional
    public List<CustomerResponse> findAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .map(this::mapToCustomerResponse)
                .toList();
    }

    @Transactional
    public void deleteCustomer(Long Id) {
        customerRepository.deleteById(Id);
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()  // getter method
        );
    }
}

// Above method set to private because access is only within class