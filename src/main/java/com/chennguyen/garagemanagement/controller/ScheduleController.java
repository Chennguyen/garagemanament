package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.ShiftAssignmentRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.ScheduleNotificationResponse;
import com.chennguyen.garagemanagement.DTO.response.WorkScheduleResponse;
import com.chennguyen.garagemanagement.entity.ScheduleNotification;
import com.chennguyen.garagemanagement.entity.WorkSchedule;
import com.chennguyen.garagemanagement.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    /// 1. Gán ca (Tạo hoặc Sửa)
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<WorkScheduleResponse> assignShift(@RequestBody ShiftAssignmentRequest request) {
        return ApiResponse.<WorkScheduleResponse>builder()
                .result(scheduleService.assignShift(request))
                .build();
    }

    // 2. Lấy lịch tháng (Grid View)
    @GetMapping
    public ApiResponse<List<WorkScheduleResponse>> getMonthlySchedule(@RequestParam int month, @RequestParam int year) {
        return ApiResponse.<List<WorkScheduleResponse>>builder()
                .result(scheduleService.getMonthlySchedule(month, year))
                .build();
    }

    // 3. Validate coverage (Giữ nguyên Boolean)
    @GetMapping("/validate-coverage")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<Boolean> validateCoverage(@RequestParam LocalDate date) {
        boolean isValid = scheduleService.validateDailyCoverage(date);
        return ApiResponse.<Boolean>builder()
                .result(isValid)
                .message(isValid ? "Nhân sự đầy đủ" : "Thiếu Manager/Cashier hoặc Mechanic!")
                .build();
    }

    // 4. Ghim thông báo
    @PostMapping("/pin-message")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ScheduleNotificationResponse> pinMessage(@RequestBody String content) {
        return ApiResponse.<ScheduleNotificationResponse>builder()
                .result(scheduleService.pinMessage(content))
                .build();
    }

    // 5. Lấy thông báo
    @GetMapping("/pin-message")
    public ApiResponse<ScheduleNotificationResponse> getPinMessage() {
        return ApiResponse.<ScheduleNotificationResponse>builder()
                .result(scheduleService.getPinnedMessage())
                .build();
    }
}
