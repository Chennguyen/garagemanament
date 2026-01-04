package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.response.CustomerResponse;
import com.chennguyen.garagemanagement.emuns.Role;
import com.chennguyen.garagemanagement.entity.Account;
import com.chennguyen.garagemanagement.entity.Customer;
import com.chennguyen.garagemanagement.repository.CustomerRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {
    CustomerRepository customerRepository;
    ModelMapper modelMapper;
    //PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerResponse registerCustomer(CustomerRegistrationRequest request) {
        // Validate
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists!");
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Confirm password does not match!");
        }

        // Map DTO -> Entity
        Customer customer = modelMapper.map(request, Customer.class);

        // Create Account
        Account account = new Account();
        account.setUsername(request.getPhoneNumber());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(Role.CUSTOMER);

        customer.setAccount(account);

        // Save
        Customer savedCustomer = customerRepository.save(customer);

        // Map Entity -> Response
        CustomerResponse response = modelMapper.map(savedCustomer, CustomerResponse.class);
        response.setRole(savedCustomer.getAccount().getRole());

        return response;
    }
}
