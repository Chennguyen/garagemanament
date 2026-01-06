package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.CustomerUpdateRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.CustomerResponse;
import com.chennguyen.garagemanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    final CustomerService customerService;

    @PostMapping("/register")
    public ApiResponse<CustomerResponse> register(@RequestBody CustomerRegistrationRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.registerCustomer(request))
                .build();
    }

    // Customer tự xem thông tin mình
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CustomerResponse> getMyProfile() {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getMyProfile())
                .build();
    }

    // Customer tự sửa thông tin mình (Tên, Địa chỉ, Gender, Dob)
    @PutMapping("/my-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<CustomerResponse> updateMyProfile(@RequestBody CustomerUpdateRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.updateCustomer(request))
                .build();
    }
}
