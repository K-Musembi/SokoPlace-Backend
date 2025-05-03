package com.sokoplace.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokoplace.customer.dto.CustomerRequest;
import com.sokoplace.customer.dto.CustomerResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private final CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerRequest validCustomerRequest;
    private CustomerRequest validCustomerRequest2;
    private CustomerRequest invalidCustomerRequest;
    private CustomerResponse customerResponse1;
    private CustomerResponse customerResponse2;

    public CustomerControllerTest(CustomerService customerService) {
        this.customerService = customerService;
    }

    @BeforeEach
    void setup() {
        validCustomerRequest = new CustomerRequest("test", "test@gmail.com");
        validCustomerRequest2 = new CustomerRequest("test2", "test2@gmail.com");
        invalidCustomerRequest = new CustomerRequest("", "");
        customerResponse1 = new CustomerResponse(1L, "test", "test@gmail.com");
        customerResponse2 = new CustomerResponse(2L, "test2", "test2@gmail.com");
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return customer if found")
    void getCustomerById() throws Exception {
        Long customerId = 1L;

        given(customerService.findCustomerById(customerId)).willReturn(customerResponse1);

        mockMvc.perform(get("/api/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerResponse1.Id()))
                .andExpect(jsonPath("$.name").value(customerResponse1.name()))
                .andExpect(jsonPath("$.email").value(customerResponse1.email()));

        verify(customerService).findCustomerById(customerId);
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return 404 if customer not found")
    void notFoundResponse() throws Exception {
        Long customerId = 99L;
        given(customerService.findCustomerById(customerId)).willThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customerService).findCustomerById(customerId);
    }

    @Test
    @DisplayName("GET /api/customers - Should return all customers")
    void getAllCustomers() throws Exception {
        given(customerService.findAllCustomers()).willReturn(List.of(customerResponse1, customerResponse2));

        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(customerResponse1.name()))
                .andExpect(jsonPath("$[1].name").value(customerResponse2.name()))
                .andExpect(jsonPath("$.length()").value(2));

        verify(customerService).findAllCustomers();
    }

    @Test
    @DisplayName("POST /api/customers - Should create and return new customer")
    void createCustomer() throws Exception {
        given(customerService.createCustomer(validCustomerRequest)).willReturn(customerResponse1);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerResponse1.Id()))
                .andExpect(jsonPath("$.name").value(customerResponse1.name()));

        verify(customerService).createCustomer(validCustomerRequest);
    }

    @Test
    @DisplayName("POST /api/customers - Should return 400 if request is invalid")
    void invalidRequest() throws Exception {
        // given is not used: customerService.createCustomer is not called because customerRequest DTO validation fails

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCustomerRequest)))
                .andExpect(status().isBadRequest());  // HTTP 400

        verify(customerService, never()).createCustomer(invalidCustomerRequest);
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Should update and return updated customer")
    void updateCustomer() throws Exception {
        Long customerId = 2L;
        given(customerService.updateCustomer(customerId, validCustomerRequest2)).willReturn(customerResponse2);

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerResponse2.Id()))
                .andExpect(jsonPath("$.name").value(customerResponse2.name()));

        verify(customerService).updateCustomer(customerId, validCustomerRequest);
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - Should return 204 if successfully deleted")
    void deleteCustomer() throws Exception {
        Long customerId = 1L;

        mockMvc.perform(delete("/api/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(customerId);
    }
}
