package com.example.customeridentitydemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private AddressType addressType;
    private Customer customer;
}

