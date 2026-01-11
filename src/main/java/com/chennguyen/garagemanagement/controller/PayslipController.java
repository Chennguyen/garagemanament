package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.PayslipResponse;
import com.chennguyen.garagemanagement.service.PayslipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayslipController {
    
    final PayslipService payslipService;

    // 👇 API Generate Payslip (Cho Admin/Manager tạo phiếu lương)
    @PostMapping("/generate/{employeeCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse<PayslipResponse> generatePayslip(
            @PathVariable String employeeCode,
            @RequestParam(required = false) Double totalHours) {
        return ApiResponse.<PayslipResponse>builder()
                .result(payslipService.generatePayslip(employeeCode, totalHours))
                .build();
    }

    // 👇 API Get Payslip by Employee Code (Cho Admin/Manager hoặc nhân viên xem phiếu lương)
    @GetMapping("/{employeeCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MECHANIC', 'CASHIER', 'SECURITY')")
    public ApiResponse<List<PayslipResponse>> getPayslipByEmployeeCode(@PathVariable String employeeCode) {
        return ApiResponse.<List<PayslipResponse>>builder()
                .result(payslipService.getPayslipByEmployeeCode(employeeCode))
                .build();
    }
}
