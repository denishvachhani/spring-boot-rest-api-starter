package com.example.customeridentitydemo.controller;

import com.example.customeridentitydemo.dto.AddressRequestDTO;
import com.example.customeridentitydemo.dto.AddressResponseDTO;
import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.exception.ResourceNotFoundException;
import com.example.customeridentitydemo.model.AddressType;
import com.example.customeridentitydemo.model.CustomerStatus;
import com.example.customeridentitydemo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private CustomerResponseDTO customerResponseDTO;
    private CustomerRequestDTO customerRequestDTO;
    private AddressResponseDTO addressResponseDTO;
    private AddressRequestDTO addressRequestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        addressResponseDTO = new AddressResponseDTO(
                1L, "123 Main St", "Anytown", "CA", "90210", AddressType.HOME
        );

        addressRequestDTO = new AddressRequestDTO(
                "456 Oak Ave", "Otherville", "NY", "10001", AddressType.BILLING
        );

        customerResponseDTO = new CustomerResponseDTO(
                1L, "John", "Doe", "john.doe@example.com", "123-45-678", "555-1234",
                CustomerStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), Collections.singletonList(addressResponseDTO), null
        );

        customerRequestDTO = new CustomerRequestDTO(
                "Jane", "Doe", "jane.doe@example.com", "987-65-432", "555-5678",
                Collections.singletonList(addressRequestDTO)
        );
    }

    @Test
    @WithMockUser
    void getAllCustomers_shouldReturnListOfCustomerResponseDTOs() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customerResponseDTO));

        mockMvc.perform(get("/api/customers")
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].addresses", hasSize(1)))
                .andExpect(jsonPath("$[0].addresses[0].street", is("123 Main St")));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    @WithMockUser
    void createCustomer_shouldReturnCreatedCustomerResponseDTO() throws Exception {
        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponseDTO);

        mockMvc.perform(post("/api/customers")
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .with(csrf()) // Add CSRF token for POST requests
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.addresses", hasSize(1)))
                .andExpect(jsonPath("$.addresses[0].street", is("123 Main St")));

        verify(customerService, times(1)).createCustomer(any(CustomerRequestDTO.class));
    }

    @Test
    @WithMockUser
    void createCustomer_shouldReturnBadRequest_whenValidationFails() throws Exception {
        CustomerRequestDTO invalidDto = new CustomerRequestDTO(
                "", "", "invalid-email", "", "", Collections.emptyList()
        );

        mockMvc.perform(post("/api/customers")
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .with(csrf()) // Add CSRF token for POST requests
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }

    @Test
    @WithMockUser
    void getCustomerById_shouldReturnCustomerResponseDTO_whenCustomerExists() throws Exception {
        when(customerService.getCustomerById(anyLong())).thenReturn(customerResponseDTO);

        mockMvc.perform(get("/api/customers/{id}", 1L)
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.addresses", hasSize(1)))
                .andExpect(jsonPath("$.addresses[0].street", is("123 Main St")));

        verify(customerService, times(1)).getCustomerById(anyLong());
    }

    @Test
    @WithMockUser
    void getCustomerById_shouldReturnNotFound_whenCustomerDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Customer not found")).when(customerService).getCustomerById(anyLong());

        mockMvc.perform(get("/api/customers/{id}", 99L)
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(customerService, times(1)).getCustomerById(anyLong());
    }

    @Test
    @WithMockUser
    void deleteCustomer_shouldReturnNoContent_whenCustomerExists() throws Exception {
        doNothing().when(customerService).deleteCustomer(anyLong());

        mockMvc.perform(delete("/api/customers/{id}", 1L)
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .with(csrf()) // Add CSRF token for DELETE requests
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(anyLong());
    }

    @Test
    @WithMockUser
    void deleteCustomer_shouldReturnNotFound_whenCustomerDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Customer not found")).when(customerService).deleteCustomer(anyLong());

        mockMvc.perform(delete("/api/customers/{id}", 99L)
                        .with(user("user").password("password").roles("USER")) // Explicitly set user
                        .with(csrf()) // Add CSRF token for DELETE requests
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(customerService, times(1)).deleteCustomer(anyLong());
    }
}
