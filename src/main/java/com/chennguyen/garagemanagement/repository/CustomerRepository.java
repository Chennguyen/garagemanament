package com.chennguyen.garagemanagement.repository;

import com.chennguyen.garagemanagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
