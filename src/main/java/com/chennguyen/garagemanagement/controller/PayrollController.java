package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.PayrollCalculationRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.PayslipResponse;
import com.chennguyen.garagemanagement.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayrollController {
    
    final PayrollService payrollService;

    // 👇 API Generate Payslip (Cho Admin/Manager tạo phiếu lương)
    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<PayslipResponse> calculatePayslip(@RequestBody PayrollCalculationRequest request) {
        return ApiResponse.<PayslipResponse>builder()
                .result(payrollService.calculatePayslip(request))
                .build();
    }

    // 2. Nhân viên tự xem lương
    @GetMapping("/my-history")
    public ApiResponse<List<PayslipResponse>> getMyPayslips() {
        return ApiResponse.<List<PayslipResponse>>builder()
                .result(payrollService.getMyPayslips())
                .build();
    }

    // 3. Admin xem lương của NV khác
    @GetMapping("/history/{employeeCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<List<PayslipResponse>> getStaffHistory(@PathVariable String employeeCode) {
        return ApiResponse.<List<PayslipResponse>>builder()
                .result(payrollService.getPayslipsByEmployeeCode(employeeCode))
                .build();
    }
}
