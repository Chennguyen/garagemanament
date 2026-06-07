package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.KioskCheckInRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.AttendanceResponse;
import com.chennguyen.garagemanagement.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Controller", description = "Quản lý chấm công (Timekeeping) qua Kiosk/Máy quét")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/kiosk")
    @Operation(
            summary = "Chấm công tại Kiosk (Check-in/Check-out)",
            description = "API dành cho thiết bị chấm công (Máy tính bảng/Máy vân tay/RFID). " +
                    "Hệ thống sẽ tự động xác định xem đây là Check-in (nếu chưa vào) hay Check-out (nếu đang làm việc) dựa trên lịch sử trong ngày."
    )
    public ApiResponse<AttendanceResponse> kioskCheck(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin định danh (Mã NV, Mã thẻ, hoặc QR Code)")
            @RequestBody KioskCheckInRequest request) {
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.handleKioskAttendance(request))
                .build();
    }
}