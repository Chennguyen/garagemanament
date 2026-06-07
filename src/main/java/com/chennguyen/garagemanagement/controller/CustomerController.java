package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.CustomerRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.CustomerUpdateRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.CustomerResponse;
import com.chennguyen.garagemanagement.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "Quản lý thông tin Khách hàng (Đăng ký, Xem/Sửa profile)")
public class CustomerController {

    final CustomerService customerService;

    @PostMapping("/register")
    @Operation(
            summary = "Đăng ký tài khoản Khách hàng",
            description = "API public cho khách vãng lai đăng ký tài khoản mới. Sau khi đăng ký thành công sẽ có thông tin để đăng nhập."
    )
    public ApiResponse<CustomerResponse> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.registerCustomer(request))
                .build();
    }

    // Customer tự xem thông tin mình
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Xem hồ sơ cá nhân (My Profile)",
            description = "Lấy thông tin chi tiết của khách hàng đang đăng nhập hiện tại based on Token.",
            security = @SecurityRequirement(name = "bearerAuth") // Icon ổ khóa trong Swagger
    )
    public ApiResponse<CustomerResponse> getMyProfile() {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getMyProfile())
                .build();
    }

    // Customer tự sửa thông tin mình
    @PutMapping("/my-profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Cập nhật hồ sơ cá nhân",
            description = "Cho phép khách hàng tự sửa các thông tin cơ bản (Tên, Ngày sinh, Giới tính, Địa chỉ...).",
            security = @SecurityRequirement(name = "bearerAuth") // Icon ổ khóa trong Swagger
    )
    public ApiResponse<CustomerResponse> updateMyProfile(@Valid @RequestBody CustomerUpdateRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.updateCustomer(request))
                .build();
    }
}