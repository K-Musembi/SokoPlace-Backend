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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser // Simulate an authenticated user for all tests (Spring Security)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerRequest validCustomerRequest;
    private CustomerRequest invalidCustomerRequest;
    private CustomerResponse customerResponse1;
    private CustomerResponse customerResponse2;

    @BeforeEach
    void setup() {
        validCustomerRequest = new CustomerRequest("test", "test@gmail.com");
        // Assuming @Valid on the DTO will handle this.
        invalidCustomerRequest = new CustomerRequest("", "not-a-valid-email");
        customerResponse1 = new CustomerResponse(1L, "test", "test@gmail.com");
        customerResponse2 = new CustomerResponse(2L, "test2", "test2@gmail.com");
    }

    // --- GET /api/v1/customers/{id} ---

    @Test
    @DisplayName("GET /api/v1/customers/{id} - Should return customer if found")
    void getCustomerById_whenCustomerExists_shouldReturnCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.findCustomerById(customerId)).willReturn(customerResponse1);

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Id").value(customerResponse1.Id()))
                .andExpect(jsonPath("$.name").value(customerResponse1.name()))
                .andExpect(jsonPath("$.email").value(customerResponse1.email()));

        verify(customerService).findCustomerById(customerId);
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} - Should return 404 Not Found if customer does not exist")
    void getCustomerById_whenCustomerDoesNotExist_shouldReturnNotFound() throws Exception {
        Long customerId = 99L;
        given(customerService.findCustomerById(customerId)).willThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound());

        verify(customerService).findCustomerById(customerId);
    }

    // --- GET /api/v1/customers/email/{email} ---

    @Test
    @DisplayName("GET /api/v1/customers/email/{email} - Should return customer if found")
    void getCustomerByEmail_whenCustomerExists_shouldReturnCustomer() throws Exception {
        String customerEmail = "test@gmail.com";
        given(customerService.findCustomerByEmail(customerEmail)).willReturn(customerResponse1);

        mockMvc.perform(get("/api/v1/customers/email/{email}", customerEmail))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Id").value(customerResponse1.Id()))
                .andExpect(jsonPath("$.name").value(customerResponse1.name()))
                .andExpect(jsonPath("$.email").value(customerResponse1.email()));

        verify(customerService).findCustomerByEmail(customerEmail);
    }

    @Test
    @DisplayName("GET /api/v1/customers/email/{email} - Should return 404 Not Found if customer does not exist")
    void getCustomerByEmail_whenCustomerDoesNotExist_shouldReturnNotFound() throws Exception {
        String customerEmail = "nonexistent@gmail.com";
        given(customerService.findCustomerByEmail(customerEmail)).willThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/email/{email}", customerEmail))
                .andExpect(status().isNotFound());

        verify(customerService).findCustomerByEmail(customerEmail);
    }

    // --- GET /api/v1/customers ---

    @Test
    @DisplayName("GET /api/v1/customers - Should return a list of all customers")
    void getAllCustomers_shouldReturnListOfCustomers() throws Exception {
        List<CustomerResponse> customers = List.of(customerResponse1, customerResponse2);
        given(customerService.findAllCustomers()).willReturn(customers);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[1].name").value("test2"));

        verify(customerService).findAllCustomers();
    }

    @Test
    @DisplayName("GET /api/v1/customers - Should return an empty list when no customers exist")
    void getAllCustomers_whenNoCustomers_shouldReturnEmptyList() throws Exception {
        given(customerService.findAllCustomers()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));

        verify(customerService).findAllCustomers();
    }

    // --- POST /api/v1/customers ---

    @Test
    @DisplayName("POST /api/v1/customers - Should create a new customer with valid data")
    void createCustomer_withValidRequest_shouldReturnCreated() throws Exception {
        given(customerService.createCustomer(any(CustomerRequest.class))).willReturn(customerResponse1);

        mockMvc.perform(post("/api/v1/customers")
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

        verify(customerService).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/customers - Should return 400 Bad Request with invalid data")
    void createCustomer_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        // The @Valid annotation on the controller method triggers validation.
        // We don't need to mock the service here as the request won't even reach it.
        mockMvc.perform(post("/api/v1/customers")
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomerRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    // --- PUT /api/v1/customers/{id} ---

    @Test
    @DisplayName("PUT /api/v1/customers/{id} - Should update an existing customer")
    void updateCustomer_whenCustomerExists_shouldReturnOk() throws Exception {
        Long customerId = 1L;
        given(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class))).willReturn(customerResponse1);

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

        verify(customerService).updateCustomer(eq(customerId), any(CustomerRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/customers/{id} - Should return 404 Not Found if customer does not exist")
    void updateCustomer_whenCustomerDoesNotExist_shouldReturnNotFound() throws Exception {
        Long customerId = 99L;
        given(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class)))
                .willThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/{id}", customerId)
                        .with(csrf()) // Add CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCustomerRequest)))
                .andExpect(status().isNotFound());

        verify(customerService).updateCustomer(eq(customerId), any(CustomerRequest.class));
    }

    // --- DELETE /api/v1/customers/{id} ---

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} - Should delete an existing customer")
    void deleteCustomer_whenCustomerExists_shouldReturnNoContent() throws Exception {
        Long customerId = 1L;
        // doNothing is the default for void methods, but it's good for readability
        doNothing().when(customerService).deleteCustomer(customerId);

        mockMvc.perform(delete("/api/v1/customers/{id}", customerId)
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(customerId);
    }

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} - Should return 404 Not Found if customer does not exist")
    void deleteCustomer_whenCustomerDoesNotExist_shouldReturnNotFound() throws Exception {
        Long customerId = 99L;
        doThrow(new EntityNotFoundException("Customer not found")).when(customerService).deleteCustomer(customerId);

        mockMvc.perform(delete("/api/v1/customers/{id}", customerId)
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isNotFound());

        verify(customerService).deleteCustomer(customerId);
    }
}
