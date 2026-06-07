package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.ShiftAssignmentRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.ScheduleNotificationResponse;
import com.chennguyen.garagemanagement.DTO.response.WorkScheduleResponse;
import com.chennguyen.garagemanagement.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule Controller", description = "Quản lý Lịch làm việc, Phân ca & Thông báo nội bộ")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. Gán ca (Tạo hoặc Sửa)
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Phân ca làm việc (Assign Shift)",
            description = "Admin/Manager gán một nhân viên vào một ca cụ thể (Sáng/Chiều) trong ngày. Nếu đã có rồi thì update.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<WorkScheduleResponse> assignShift(@RequestBody ShiftAssignmentRequest request) {
        return ApiResponse.<WorkScheduleResponse>builder()
                .result(scheduleService.assignShift(request))
                .build();
    }

    // 2. Lấy lịch tháng (Grid View)
    @GetMapping
    @Operation(
            summary = "Xem lịch làm việc theo tháng (Calendar View)",
            description = "Lấy toàn bộ lịch làm việc của tất cả nhân viên trong tháng được chọn để hiển thị lên lưới lịch.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<List<WorkScheduleResponse>> getMonthlySchedule(
            @Parameter(description = "Tháng cần xem (1-12)", example = "10") @RequestParam int month,
            @Parameter(description = "Năm cần xem", example = "2025") @RequestParam int year) {
        return ApiResponse.<List<WorkScheduleResponse>>builder()
                .result(scheduleService.getMonthlySchedule(month, year))
                .build();
    }

    // 3. Validate coverage
    @GetMapping("/validate-coverage")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Kiểm tra định mức nhân sự (Coverage)",
            description = "Kiểm tra xem ngày hôm đó đã đủ người trực chưa (VD: Phải có ít nhất 1 Manager/Cashier và 1 Mechanic).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<Boolean> validateCoverage(
            @Parameter(description = "Ngày cần kiểm tra (YYYY-MM-DD)") @RequestParam LocalDate date) {
        boolean isValid = scheduleService.validateDailyCoverage(date);
        return ApiResponse.<Boolean>builder()
                .result(isValid)
                .message(isValid ? "Nhân sự đầy đủ" : "Thiếu Manager/Cashier hoặc Mechanic!")
                .build();
    }

    // 4. Ghim thông báo
    @PostMapping("/pin-message")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Ghim thông báo lên bảng lịch",
            description = "Tạo một ghi chú chung (Sticky Note) hiển thị trên đầu bảng lịch (VD: 'Tháng này tổng vệ sinh ngày 15').",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<ScheduleNotificationResponse> pinMessage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nội dung thông báo")
            @RequestBody String content) {
        return ApiResponse.<ScheduleNotificationResponse>builder()
                .result(scheduleService.pinMessage(content))
                .build();
    }

    // 5. Lấy thông báo
    @GetMapping("/pin-message")
    @Operation(
            summary = "Xem thông báo được ghim",
            description = "Lấy nội dung thông báo hiện tại đang được ghim để hiển thị cho nhân viên thấy.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ApiResponse<ScheduleNotificationResponse> getPinMessage() {
        return ApiResponse.<ScheduleNotificationResponse>builder()
                .result(scheduleService.getPinnedMessage())
                .build();
    }
}