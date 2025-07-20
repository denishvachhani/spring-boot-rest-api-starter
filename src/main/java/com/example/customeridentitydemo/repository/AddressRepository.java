package com.example.customeridentitydemo.repository;

import com.example.customeridentitydemo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
