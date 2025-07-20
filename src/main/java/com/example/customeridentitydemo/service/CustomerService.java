package com.example.customeridentitydemo.service;

import com.example.customeridentitydemo.dto.AddressRequestDTO;
import com.example.customeridentitydemo.dto.AddressResponseDTO;
import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.exception.ResourceNotFoundException;
import com.example.customeridentitydemo.model.Address;
import com.example.customeridentitydemo.model.AddressType;
import com.example.customeridentitydemo.model.Customer;
import com.example.customeridentitydemo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::convertToCustomerDto);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setSsn(customerRequestDTO.getSsn());
        customer.setPhone(customerRequestDTO.getPhone());

        // Handle addresses
        if (customerRequestDTO.getAddresses() != null) {
            customerRequestDTO.getAddresses().forEach(addressDto -> {
                Address address = convertToAddressEntity(addressDto);
                customer.addAddress(address); // Use helper to set bidirectional relationship
            });
        }

        Customer savedCustomer = customerRepository.save(customer);
        return convertToCustomerDto(savedCustomer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToCustomerDto(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        existingCustomer.setFirstName(customerRequestDTO.getFirstName());
        existingCustomer.setLastName(customerRequestDTO.getLastName());
        existingCustomer.setEmail(customerRequestDTO.getEmail());
        existingCustomer.setSsn(customerRequestDTO.getSsn());
        existingCustomer.setPhone(customerRequestDTO.getPhone());

        // Handle addresses update
        // Clear existing addresses and add new ones from DTO
        existingCustomer.getAddresses().clear();
        if (customerRequestDTO.getAddresses() != null) {
            customerRequestDTO.getAddresses().forEach(addressDto -> {
                Address address = convertToAddressEntity(addressDto);
                existingCustomer.addAddress(address);
            });
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToCustomerDto(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setDeletedAt(LocalDateTime.now()); // Soft delete
        customerRepository.save(customer);
    }

    @Transactional
    public void populateTimestampsForExistingCustomers() {
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            if (customer.getCreatedAt() == null) {
                customer.setCreatedAt(LocalDateTime.now());
            }
            // Saving an existing entity triggers the @LastModifiedDate update
            customerRepository.save(customer);
        }
    }

    private CustomerResponseDTO convertToCustomerDto(Customer customer) {
        List<AddressResponseDTO> addressDtos = customer.getAddresses().stream()
                .map(this::convertToAddressDto)
                .collect(Collectors.toList());

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getSsn(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                addressDtos
        );
    }

    private Address convertToAddressEntity(AddressRequestDTO addressDto) {
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setZipCode(addressDto.getZipCode());
        address.setAddressType(addressDto.getAddressType());
        return address;
    }

    private AddressResponseDTO convertToAddressDto(Address address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getAddressType()
        );
    }

    @Transactional
    public void generateDummyData() {
        for (int i = 1; i <= 40; i++) {
            CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO(
                    "FirstName" + i,
                    "LastName" + i,
                    "email" + i + "@example.com",
                    "SSN" + i,
                    "123-456-78" + String.format("%02d", i),
                    null // Addresses will be added below
            );

            // Add 1 to 3 random addresses for each customer
            int numAddresses = (int) (Math.random() * 3) + 1; // 1, 2, or 3 addresses
            List<AddressRequestDTO> addresses = new ArrayList<>();
            for (int j = 0; j < numAddresses; j++) {
                addresses.add(new AddressRequestDTO(
                        "Street" + i + "-" + j,
                        "City" + i,
                        "State" + (char)('A' + (int)(Math.random() * 26)),
                        "" + (10000 + (int)(Math.random() * 89999)),
                        AddressType.values()[(int)(Math.random() * AddressType.values().length)]
                ));
            }
            customerRequestDTO.setAddresses(addresses);

            createCustomer(customerRequestDTO);
        }
    }
}