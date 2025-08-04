package com.example.customeridentitydemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String ssn;
    private String phone;
    private CustomerStatus status = CustomerStatus.PENDING_VERIFICATION;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Address> addresses = new ArrayList<>();

    // Helper method to manage bidirectional relationship
    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
}

