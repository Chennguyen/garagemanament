package com.chennguyen.garagemanagement.controller;

import com.chennguyen.garagemanagement.DTO.request.KioskCheckInRequest;
import com.chennguyen.garagemanagement.DTO.response.ApiResponse;
import com.chennguyen.garagemanagement.DTO.response.AttendanceResponse;
import com.chennguyen.garagemanagement.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/kiosk")
    public ApiResponse<AttendanceResponse> kioskCheck(@RequestBody KioskCheckInRequest request) {
        return ApiResponse.<AttendanceResponse>builder()
                .result(attendanceService.handleKioskAttendance(request)) // Service trả về DTO luôn rồi
                .build();
    }
}
