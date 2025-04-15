package com.sokoplace.customer;

import com.sokoplace.customer.dto.CustomerRequest;
import com.sokoplace.customer.dto.CustomerResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//  Though you could inject repositories directly into controllers,
//  it is best practice to have a service layer.

//  Service layer incorporates business logic and coordinates
//  interactions with more than one repository.
// @Service: Marks interface as a Spring Bean (good practice)
// @Autowired: Constructor injection. Use for constructors and setters
// mapCustomerToCustomerResponse(): Helper method to map Entity -> CustomerResponse
// Stream<>: accepts elements from List, etc. but doesn't store. For complex operations
// Stream operations: map, filter, reduce, find, sort, collect

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());
        customer.setPassword(customerRequest.password());  // setter method

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setName(customerRequest.name());
        customer.setEmail(customerRequest.email());
        customer.setPassword(customerRequest.password());

        return customerRepository.save(customer);
    }

    @Transactional
    public CustomerResponse findCustomerById(Long Id) {
        Customer customer = customerRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse findCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToCustomerResponse(customer);
    }

    @Transactional
    public List<CustomerResponse> findCustomersBySubString(String name) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name);

        return customers.stream()
                .map(this::mapToCustomerResponse)
                .toList();
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        CustomerResponse responseDTO = new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail()  // getter method
        );

        return responseDTO;
    }
}

// Above method set to private because access is only within class