package com.sokoplace.customer;

import com.sokoplace.customer.dto.CustomerRequest;
import com.sokoplace.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ResponseEntity represents the HTTP response: status code, headers, body (payload)
// ResponseEntity.ok(createdProduct) -> 200 Ok, ResponseEntity.status(HttpStatus.CREATED).body(savedProduct) -> 201 created
// ResponseEntity.notFound(), ResponseEntity.badRequest().body("Invalid"), etc.
// Create a custom header object, then ResponseEntity.ok().headers(customHeaders).body(...)
// @Valid: corresponds to validation in DTO; for incoming @ResponseBody

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("search/{Id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long Id) {
        CustomerResponse responseObject = customerService.findCustomerById(Id);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("/search/{email}")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@PathVariable String email) {
        CustomerResponse responseObject = customerService.findCustomerByEmail(email);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("/search/all")
    public ResponseEntity<List<CustomerResponse>> getAllCustomer() {
        List<CustomerResponse> responseObject = customerService.findAllCustomers();
        return ResponseEntity.ok(responseObject);
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse responseObject = customerService.createCustomer(customerRequest);
        //return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @PutMapping("/update/{Id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long Id,
            @Valid @RequestBody CustomerRequest customerRequest) {
        CustomerResponse responseObject = customerService.updateCustomer(Id, customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long Id) {
        customerService.deleteCustomer(Id);
        return ResponseEntity.noContent().build();
    }

}
