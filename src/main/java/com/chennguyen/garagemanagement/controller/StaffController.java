package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.StaffRegistrationRequest;
import com.chennguyen.garagemanagement.DTO.request.StaffUpdateRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.SalaryHistoryResponse;
import com.chennguyen.garagemanagement.DTO.response.StaffResponse;
import com.chennguyen.garagemanagement.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
@Tag(name = "Staff Controller", description = "Quản lý nhân viên nội bộ (Admin, Manager, Mechanic, Cashier...)")
public class StaffController {

    final StaffService staffService;

    // API Tạo nhân viên
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
            summary = "Tạo nhân viên mới (Onboarding)",
            description = "Chỉ dành cho ADMIN hoặc MANAGER. Dùng để tạo tài khoản cho nhân viên mới vào làm (Thợ, Thu ngân...).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<StaffResponse> registerStaff(@RequestBody StaffRegistrationRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.createStaff(request))
                .build();
    }

    // API Xem Profile (Cá nhân)
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Xem hồ sơ nhân viên (Của chính mình)",
            description = "Bất kỳ nhân viên nào đã đăng nhập đều xem được thông tin của chính họ (My Profile).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<StaffResponse> getMyProfile() {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.getMyProfile())
                .build();
    }

    // API Update Profile (Cá nhân)
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Cập nhật hồ sơ nhân viên (Của chính mình)",
            description = "Nhân viên tự cập nhật thông tin cá nhân (SĐT, Địa chỉ...). Không được sửa Lương hay Chức vụ.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<StaffResponse> updateProfile(@RequestBody StaffUpdateRequest request) {
        return ApiResponse.<StaffResponse>builder()
                .result(staffService.updateProfile(request))
                .build();
    }

    // API Xem lịch sử lương (Cá nhân)
    @GetMapping("/salary-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    @Operation(
            summary = "Xem lịch sử lương (Của chính mình)",
            description = "Nhân viên tự xem lại lịch sử nhận lương của bản thân qua các tháng.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<SalaryHistoryResponse>> getMySalaryHistory() {
        return ApiResponse.<List<SalaryHistoryResponse>>builder()
                .result(staffService.getMySalaryHistory())
                .build();
    }

    // API Xem lịch sử lương người khác (Quyền Admin)
    @GetMapping("/{staffId}/salary-history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
            summary = "Xem lịch sử lương của nhân viên bất kỳ",
            description = "Chỉ ADMIN hoặc MANAGER mới được quyền soi lịch sử lương của một nhân viên cụ thể theo ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<SalaryHistoryResponse>> getSalaryHistoryByStaffId(
            @Parameter(description = "ID của nhân viên cần xem lương") @PathVariable String staffId) {
        return ApiResponse.<List<SalaryHistoryResponse>>builder()
                .result(staffService.getSalaryHistoryByStaffId(staffId))
                .build();
    }
}