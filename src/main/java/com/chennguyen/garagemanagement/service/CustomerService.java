package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.response.CustomerResponse;
import com.chennguyen.garagemanagement.entity.Account;
import com.chennguyen.garagemanagement.entity.Customer;
import com.chennguyen.garagemanagement.entity.Role;
import com.chennguyen.garagemanagement.exception.AppException;
import com.chennguyen.garagemanagement.exception.ErrorCode;
import com.chennguyen.garagemanagement.repository.CustomerRepository;
import com.chennguyen.garagemanagement.repository.RoleRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    RoleRepository roleRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerResponse registerCustomer(CustomerRegistrationRequest request) {
        // 1. Validate Input (Dùng AppException)
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 2. Fetch ROLE_CUSTOMER from DB
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // 3. Map DTO -> Customer Entity
        Customer customer = modelMapper.map(request, Customer.class);

        // 4. Create Account
        Account account = new Account();
        account.setUsername(request.getPhoneNumber()); // Username is Phone Number
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEnabled(true);

        account.setRole(customerRole);

        // 5. Link Account to Customer
        customer.setAccount(account);

        // 6. Save Customer (Cascade will save Account and account_roles table)
        Customer savedCustomer = customerRepository.save(customer);

        // 7. Map to Response
        CustomerResponse response = modelMapper.map(savedCustomer, CustomerResponse.class);

        // Map Role details to Response
        if (savedCustomer.getAccount().getRole() != null) {
            response.setRoleName(savedCustomer.getAccount().getRole().getName());
            response.setRoleDescription(savedCustomer.getAccount().getRole().getDescription());
        }

        return response;
    }
}
