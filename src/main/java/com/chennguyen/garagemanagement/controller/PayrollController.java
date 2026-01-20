package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.PayrollCalculationRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.PayslipResponse;
import com.chennguyen.garagemanagement.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
@Tag(name = "Payroll Controller", description = "Quản lý Bảng lương & Tính lương hàng tháng")
public class PayrollController {

    final PayrollService payrollService;

    // 1. API Generate Payslip (Cho Admin/Manager tính lương)
    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
            summary = "Tính toán & Chốt phiếu lương (Tính lương)",
            description = "Chỉ dành cho ADMIN/MANAGER. Hệ thống sẽ tính toán lương dựa trên: Lương cơ bản + Thưởng - Phạt - Ngày nghỉ... và lưu vào Database.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<PayslipResponse> calculatePayslip(
            @RequestBody PayrollCalculationRequest request) {
        return ApiResponse.<PayslipResponse>builder()
                .result(payrollService.calculatePayslip(request))
                .build();
    }

    // 2. Nhân viên tự xem lương
    @GetMapping("/my-history")
    // Note: Nên thêm check login, ví dụ isAuthenticated() hoặc role cụ thể
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Xem phiếu lương của tôi (Cá nhân)",
            description = "Nhân viên (sau khi login) xem lại danh sách các phiếu lương đã nhận trong quá khứ.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<PayslipResponse>> getMyPayslips() {
        return ApiResponse.<List<PayslipResponse>>builder()
                .result(payrollService.getMyPayslips())
                .build();
    }

    // 3. Admin xem lương của NV khác
    @GetMapping("/history/{employeeCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Tra cứu lương nhân viên (Quản lý)",
            description = "Admin/Manager xem lịch sử trả lương của một nhân viên cụ thể thông qua Mã nhân viên (Employee Code).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<PayslipResponse>> getStaffHistory(
            @Parameter(description = "Mã nhân viên cần tra cứu (VD: STF001)") @PathVariable String employeeCode) {
        return ApiResponse.<List<PayslipResponse>>builder()
                .result(payrollService.getPayslipsByEmployeeCode(employeeCode))
                .build();
    }
}