package com.example.customeridentitydemo.service;

import com.example.customeridentitydemo.client.OrderResponseDTO;
import com.example.customeridentitydemo.client.OrderServiceClient;
import com.example.customeridentitydemo.dto.AddressRequestDTO;
import com.example.customeridentitydemo.dto.AddressResponseDTO;
import com.example.customeridentitydemo.dto.CustomerRequestDTO;
import com.example.customeridentitydemo.dto.CustomerResponseDTO;
import com.example.customeridentitydemo.exception.ResourceNotFoundException;
import com.example.customeridentitydemo.model.Address;
import com.example.customeridentitydemo.model.AddressType;
import com.example.customeridentitydemo.model.Customer;
import com.example.customeridentitydemo.repository.JdbcCustomerRepository;
import com.example.customeridentitydemo.repository.JdbcAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private JdbcCustomerRepository customerRepository;

    @Autowired
    private JdbcAddressRepository addressRepository;

    @Autowired
    private OrderServiceClient orderServiceClient;

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToCustomerDto)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setSsn(customerRequestDTO.getSsn());
        customer.setPhone(customerRequestDTO.getPhone());

        Customer savedCustomer = customerRepository.save(customer);

        // Handle addresses
        if (customerRequestDTO.getAddresses() != null) {
            customerRequestDTO.getAddresses().forEach(addressDto -> {
                Address address = convertToAddressEntity(addressDto);
                address.setCustomer(savedCustomer);
                Address savedAddress = addressRepository.save(address);
                savedCustomer.addAddress(savedAddress);
            });
        }

        return convertToCustomerDto(savedCustomer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        List<Address> addresses = addressRepository.findByCustomerId(id);
        customer.setAddresses(addresses);

        List<OrderResponseDTO> orders = Collections.emptyList();
        try {
            orders = orderServiceClient.getOrdersByCustomerId(id);
        } catch (Exception e) {
            // Log the exception, but don't fail the request if the order service is down
            // In a real-world scenario, you might want to implement a circuit breaker here
        }

        CustomerResponseDTO customerResponseDTO = convertToCustomerDto(customer);
        customerResponseDTO.setOrders(orders);
        return customerResponseDTO;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        existingCustomer.setFirstName(customerRequestDTO.getFirstName());
        existingCustomer.setLastName(customerRequestDTO.getLastName());
        existingCustomer.setEmail(customerRequestDTO.getEmail());
        existingCustomer.setSsn(customerRequestDTO.getSsn());
        existingCustomer.setPhone(customerRequestDTO.getPhone());

        Customer updatedCustomer = customerRepository.update(existingCustomer);

        // Handle addresses update
        // Clear existing addresses and add new ones from DTO
        addressRepository.findByCustomerId(id).forEach(address -> addressRepository.deleteById(address.getId()));
        if (customerRequestDTO.getAddresses() != null) {
            customerRequestDTO.getAddresses().forEach(addressDto -> {
                Address address = convertToAddressEntity(addressDto);
                address.setCustomer(updatedCustomer);
                addressRepository.save(address);
            });
        }

        return convertToCustomerDto(updatedCustomer);
    }

    public void populateTimestampsForExistingCustomers() {
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            if (customer.getCreatedAt() == null) {
                customer.setCreatedAt(LocalDateTime.now());
            }
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.update(customer);
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
                addressDtos,
                null // Orders are fetched separately
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
