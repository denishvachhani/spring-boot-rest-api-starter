package com.example.customeridentitydemo.controller;

import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "APIs for managing customer data")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all customers"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token")
    })
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Create a new customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data - validation errors"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business validation errors")
    })
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found and returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDTO> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data - validation errors"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business validation errors")
    })
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id, 
            @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerRequestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/populate-timestamps")
    @Operation(summary = "Populate timestamps", description = "Utility endpoint to populate timestamps for existing customers (development only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timestamps populated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token")
    })
    public ResponseEntity<String> populateTimestamps() {
        customerService.populateTimestampsForExistingCustomers();
        return ResponseEntity.ok("Timestamps populated for existing customers.");
    }

    @GetMapping("/generate-dummy-data")
    @Operation(summary = "Generate dummy data", description = "Utility endpoint to generate sample customer data (development only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dummy data generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid JWT token")
    })
    public ResponseEntity<String> generateDummyData() {
        customerService.generateDummyData();
        return ResponseEntity.ok("Dummy data generated successfully.");
    }
}