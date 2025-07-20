package com.example.customeridentitydemo.repository;

import com.example.customeridentitydemo.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find all active customers (where deletedAt is null)
    List<Customer> findAllByDeletedAtIsNull();

    // Find all active customers with pagination and sorting
    Page<Customer> findAllByDeletedAtIsNull(Pageable pageable);

    // Find all active customers with sorting (without pagination)
    List<Customer> findAllByDeletedAtIsNull(Sort sort);

    // Find an active customer by ID (where deletedAt is null)
    Optional<Customer> findByIdAndDeletedAtIsNull(Long id);

    // Find a customer by ID, including soft-deleted ones (for internal use if needed)
    // Optional<Customer> findById(Long id);
}
