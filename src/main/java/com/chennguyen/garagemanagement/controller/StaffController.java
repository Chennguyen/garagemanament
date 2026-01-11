package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.StaffRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.StaffUpdateRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.SalaryHistoryResponse;
import com.chennguyen.garagemanagement.DTO.response.StaffResponse;
import com.chennguyen.garagemanagement.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {
    final StaffService staffService;

    // API Tạo nhân viên
    // Chỉ cho phép ADMIN hoặc MANAGER gọi
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<StaffResponse> registerStaff(@RequestBody StaffRegistrationRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.createStaff(request))
                .build();
    }
    // 👇 API Xem Profile (Cho bất kỳ nhân viên nào đã login)
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    public ApiResponse<StaffResponse> getMyProfile() {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.getMyProfile())
                .build();
    }

    // 👇 API Update Profile (Cho nhân viên tự sửa info mình)
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    public ApiResponse<StaffResponse> updateProfile(@RequestBody StaffUpdateRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.updateProfile(request))
                .build();
    }

    // 👇 API Get Own Salary History (Cho nhân viên xem lịch sử lương của mình)
    @GetMapping("/salary-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    public ApiResponse<java.util.List<SalaryHistoryResponse>> getMySalaryHistory() {
        return ApiResponse.<java.util.List<SalaryHistoryResponse>>builder()
                .result(staffService.getMySalaryHistory())
                .build();
    }

    // 👇 API Get Salary History by Staff ID (Cho Admin/Manager xem lịch sử lương của nhân viên khác)
    @GetMapping("/{staffId}/salary-history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<java.util.List<SalaryHistoryResponse>> getSalaryHistoryByStaffId(@PathVariable String staffId) {
        return ApiResponse.<java.util.List<SalaryHistoryResponse>>builder()
                .result(staffService.getSalaryHistoryByStaffId(staffId))
                .build();
    }
}
