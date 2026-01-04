package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.CustomerResponse;
import com.chennguyen.garagemanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
