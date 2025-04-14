package com.sokoplace.customer;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// NB: Though you could inject repositories directly into controllers,
//     it is best practice to have a service layer.
//     Service layer incorporates business logic e.g. validation, and
//     coordinates interactions with more than one repository.

@Service  // Mark as Spring service bean
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired  // Constructor injection. Use for constructors and setters
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer addCustomer(Customer customer) {
        // Validation logic

        return customerRepository.save(customer);
    }
}
