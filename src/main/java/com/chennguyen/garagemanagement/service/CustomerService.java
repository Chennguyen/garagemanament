package com.chennguyen.garagemanagement.service;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.CustomerUpdateRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
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

        log.info("Customer registered successfully with ID: {}", savedCustomer.getId()); // 📝 Log thành công
        return convertToResponse(savedCustomer);
    }

    // ---  XEM PROFILE (CUSTOMER) ---
    public CustomerResponse getMyProfile() {
        Customer customer = getCurrentAuthenticatedCustomer();
        return convertToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(CustomerUpdateRequest request) {
        // Lấy SĐT từ Token
        Customer customer = getCurrentAuthenticatedCustomer();

        // ⚠️ FIX QUAN TRỌNG: Check từng trường, nếu có gửi lên mới update
        // Tránh việc modelMapper tự động set null đè lên dữ liệu cũ

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            customer.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            customer.setEmail(request.getEmail());
        }

        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            customer.setAddress(request.getAddress());
        }

        if (request.getGender() != null && !request.getGender().isBlank()) {
            customer.setGender(request.getGender());
        }

        if (request.getDob() != null) {
            customer.setDob(request.getDob());
        }

        Customer updatedCustomer = customerRepository.save(customer);

        log.info("Profile updated successfully for ID: {}", updatedCustomer.getId()); // 📝 Log thành công
        return convertToResponse(updatedCustomer);
    }

    // --- HELPER METHODS ---

    private Customer getCurrentAuthenticatedCustomer() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String phoneNumber = authentication.getName();

        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = modelMapper.map(customer, CustomerResponse.class);

        // Lấy Role từ Account lôi ra ngoài
        if (customer.getAccount() != null && customer.getAccount().getRole() != null) {
            response.setRoleName(customer.getAccount().getRole().getName());
            response.setRoleDescription(customer.getAccount().getRole().getDescription());
        }
        return response;
    }
}
