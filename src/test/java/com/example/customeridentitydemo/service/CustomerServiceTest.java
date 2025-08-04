package com.example.customeridentitydemo.service;

import com.example.customeridentitydemo.dto.AddressRequestDTO;
import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.exception.ResourceNotFoundException;
import com.example.customeridentitydemo.model.Address;
import com.example.customeridentitydemo.model.AddressType;
import com.example.customeridentitydemo.model.Customer;
import com.example.customeridentitydemo.model.CustomerStatus;
import com.example.customeridentitydemo.repository.JdbcCustomerRepository;
import com.example.customeridentitydemo.repository.JdbcAddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private JdbcCustomerRepository customerRepository;

    @Mock
    private JdbcAddressRepository addressRepository;

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
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        List<CustomerResponseDTO> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(customer.getFirstName(), result.get(0).getFirstName());
        assertEquals(1, result.get(0).getAddresses().size());
        assertEquals(address.getStreet(), result.get(0).getAddresses().get(0).getStreet());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void createCustomer_shouldReturnCustomerResponseDTO() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(addressRepository.findByCustomerId(anyLong())).thenReturn(Arrays.asList(address));

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(1, result.getAddresses().size());
        verify(customerRepository, times(1)).findById(anyLong());
        verify(addressRepository, times(1)).findByCustomerId(anyLong());
    }

    @Test
    void getCustomerById_shouldThrowResourceNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteCustomer_shouldDeleteCustomer_whenCustomerExists() {
        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).deleteById(anyLong());
    }
}
