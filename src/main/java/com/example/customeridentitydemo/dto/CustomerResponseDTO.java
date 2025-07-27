package com.example.customeridentitydemo.dto;

import com.example.customeridentitydemo.client.OrderResponseDTO;
import com.example.customeridentitydemo.model.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String ssn;
    private String phone;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddressResponseDTO> addresses;
    private List<OrderResponseDTO> orders;

    
}