package com.example.customeridentitydemo.service;

import com.example.customeridentitydemo.dto.AddressRequestDTO;
import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.exception.ResourceNotFoundException;
import com.example.customeridentitydemo.model.Address;
import com.example.customeridentitydemo.model.AddressType;
import com.example.customeridentitydemo.model.Customer;
import com.example.customeridentitydemo.model.CustomerStatus;
import com.example.customeridentitydemo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO customerRequestDTO;
    private Address address;
    private AddressRequestDTO addressRequestDTO;

    @BeforeEach
    void setUp() {
        address = new Address(
                1L, "123 Main St", "Anytown", "CA", "90210", AddressType.HOME, null
        );

        addressRequestDTO = new AddressRequestDTO(
                "456 Oak Ave", "Otherville", "NY", "10001", AddressType.BILLING
        );

        customer = new Customer(
                1L, "John", "Doe", "john.doe@example.com", "123-45-678", "555-1234",
                CustomerStatus.ACTIVE, null, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>()
        );
        customer.addAddress(address); // Use helper to set bidirectional relationship

        customerRequestDTO = new CustomerRequestDTO(
                "Jane", "Doe", "jane.doe@example.com", "987-65-432", "555-5678",
                Collections.singletonList(addressRequestDTO)
        );
    }

    @Test
    void getAllCustomers_shouldReturnListOfCustomerResponseDTOs() {
        Page<Customer> customerPage = new PageImpl<>(Arrays.asList(customer));
        when(customerRepository.findAllByDeletedAtIsNull(any(Pageable.class))).thenReturn(customerPage);

        Page<CustomerResponseDTO> result = customerService.getAllCustomers(Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(customer.getFirstName(), result.getContent().get(0).getFirstName());
        assertEquals(1, result.getContent().get(0).getAddresses().size());
        assertEquals(address.getStreet(), result.getContent().get(0).getAddresses().get(0).getStreet());
        verify(customerRepository, times(1)).findAllByDeletedAtIsNull(any(Pageable.class));
    }

    @Test
    void createCustomer_shouldReturnCustomerResponseDTO() {
        // Mock the save method to return the customer with its ID and addresses set
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setId(1L); // Simulate ID generation
            savedCustomer.getAddresses().forEach(addr -> addr.setId(2L)); // Simulate address ID generation
            return savedCustomer;
        });

        CustomerResponseDTO result = customerService.createCustomer(customerRequestDTO);

        assertNotNull(result);
        assertEquals(customerRequestDTO.getFirstName(), result.getFirstName());
        assertFalse(result.getAddresses().isEmpty());
        assertEquals(1, result.getAddresses().size());
        assertEquals(addressRequestDTO.getStreet(), result.getAddresses().get(0).getStreet());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomerById_shouldReturnCustomerResponseDTO_whenCustomerExists() {
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(1, result.getAddresses().size());
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void getCustomerById_shouldThrowResourceNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void updateCustomer_shouldReturnUpdatedCustomerResponseDTO() {
        // Simulate the existing customer having addresses
        Customer existingCustomerWithAddresses = new Customer(
                1L, "John", "Doe", "john.doe@example.com", "123-45-678", "555-1234",
                CustomerStatus.ACTIVE, null, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>()
        );
        existingCustomerWithAddresses.addAddress(new Address(3L, "Old St", "Old City", "OC", "12345", AddressType.HOME, existingCustomerWithAddresses));

        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(existingCustomerWithAddresses));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.getAddresses().forEach(addr -> {
                if (addr.getId() == null) addr.setId(4L); // Simulate new address ID generation
            });
            return savedCustomer;
        });

        CustomerResponseDTO result = customerService.updateCustomer(1L, customerRequestDTO);

        assertNotNull(result);
        assertEquals(customerRequestDTO.getFirstName(), result.getFirstName());
        assertFalse(result.getAddresses().isEmpty());
        assertEquals(1, result.getAddresses().size());
        assertEquals(addressRequestDTO.getStreet(), result.getAddresses().get(0).getStreet());
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_shouldThrowResourceNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(1L, customerRequestDTO));
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_shouldMarkCustomerAsDeleted_whenCustomerExists() {
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.deleteCustomer(1L);

        assertNotNull(customer.getDeletedAt());
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_shouldThrowResourceNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).findByIdAndDeletedAtIsNull(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
